/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurely.core.context;

import com.nowucca.shurely.core.NamedObject;
import com.nowucca.shurely.core.StringGenerator;
import com.nowucca.shurely.core.URIManager;
import com.nowucca.shurely.core.URIStore;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.nowucca.shurely.util.ResourceInjectionUtil.injectAll;
import static java.lang.String.format;
import static java.lang.Thread.currentThread;

public class URIManagerContextResolver {

    public URIManagerContext resolve(String uriManagerName) throws IllegalArgumentException {

        ConcurrentMap<String, StringGenerator> generators =
                       loadImpls(currentThread().getContextClassLoader(),
                               StringGenerator.class);

        ConcurrentMap<String, URIStore> stores =
                loadImpls(currentThread().getContextClassLoader(), URIStore.class);

        ConcurrentMap<String, URIManager> uriManagers =
                loadImpls(currentThread().getContextClassLoader(), URIManager.class);

        URIManager selectedURIManager = uriManagers.get(uriManagerName);
        if (selectedURIManager == null) {
            StringBuilder sb = new StringBuilder();
            for(String name: uriManagers.keySet()) { sb.append(name); sb.append(','); }
            if (sb.length()>0 && sb.charAt(sb.length()-1)==',') { sb.deleteCharAt(sb.length()-1); }
            throw new IllegalArgumentException(format("Unrecognized uri manager %s.  (available uri managers = %s)", uriManagerName, sb.toString()));
        }

        Map<Class<?>, Object> injectables = new HashMap<Class<?>, Object>();

        for(StringGenerator generator: generators.values()) {
            injectables.put(generator.getClass(), generator);
        }

        for(URIStore store: stores.values()) {
            injectables.put(store.getClass(), store);
        }

        for(URIManager uriManager: uriManagers.values()) {
            injectables.put(uriManager.getClass(), uriManager);
        }

        for(StringGenerator generator: generators.values()) {
            injectAll(generator, injectables);
        }

        for(URIStore store: stores.values()) {
            injectAll(store, injectables);
        }

        for(URIManager uriManager: uriManagers.values()) {
            injectAll(uriManager, injectables);
        }

        return new DefaultURIManagerContext(generators, stores, uriManagers, selectedURIManager);
    }


    private <T extends NamedObject> ConcurrentMap<String, T> loadImpls(ClassLoader classLoader, final Class<T> clazz) {
        ServiceLoader<T> loader = load(classLoader, clazz);
        ConcurrentMap<String, T> impls = new ConcurrentHashMap<String, T>();
        for(T t: loader) {
            String tName = t.getName();
            T oldT = impls.put(tName, t);
            if (oldT != null) {
                throw new RuntimeException(format("Duplicate %s name: %s", clazz, tName));
            }
        }
        return impls;
    }

    private <T> ServiceLoader<T> load(ClassLoader classLoader, Class<T> service) {
        return (classLoader != null) ? ServiceLoader.load(service, classLoader) : ServiceLoader.load(service);
    }
}
