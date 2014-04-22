/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurely.core.context;

import com.nowucca.shurely.core.URIManager;
import com.nowucca.shurely.core.URIStore;
import com.nowucca.shurely.core.UniqueStringGenerator;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class DefaultURIManagerContext implements URIManagerContext {

    private Map<String, UniqueStringGenerator> generators;
    private Map<String, URIStore> stores;
    private Map<String, URIManager> uriManagers;

    public DefaultURIManagerContext(Map<String, UniqueStringGenerator> generators, Map<String, URIStore> stores, Map<String, URIManager> uriManagers) {
        this.generators = generators;
        this.stores = stores;
        this.uriManagers = uriManagers;
    }

    public URIManager getURIManager(String name) {
        return uriManagers.get(name);
    }

    public URIStore getURIStore(String name) {
        return stores.get(name);
    }

    public UniqueStringGenerator getGenerator(String name) {
        return generators.get(name);
    }

}
