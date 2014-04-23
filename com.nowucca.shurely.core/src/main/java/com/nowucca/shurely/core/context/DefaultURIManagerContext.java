/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurely.core.context;

import com.nowucca.shurely.core.StringGenerator;
import com.nowucca.shurely.core.URIManager;
import com.nowucca.shurely.core.URIStore;

import java.util.Map;

public class DefaultURIManagerContext implements URIManagerContext {

    private Map<String, StringGenerator> generators;
    private Map<String, URIStore> stores;
    private Map<String, URIManager> uriManagers;

    public DefaultURIManagerContext(Map<String, StringGenerator> generators, Map<String, URIStore> stores, Map<String, URIManager> uriManagers) {
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

    public StringGenerator getGenerator(String name) {
        return generators.get(name);
    }

}
