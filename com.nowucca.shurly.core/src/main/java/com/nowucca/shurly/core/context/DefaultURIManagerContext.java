/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurly.core.context;

import com.nowucca.shurly.core.StringGenerator;
import com.nowucca.shurly.core.URIManager;
import com.nowucca.shurly.core.URIStore;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class DefaultURIManagerContext implements URIManagerContext {

    private Map<String, StringGenerator> generators;
    private Map<String, URIStore> stores;
    private Map<String, URIManager> uriManagers;
    private URIManager selectedURIManager;

    public DefaultURIManagerContext(Map<String, StringGenerator> generators, Map<String, URIStore> stores,
                                    Map<String, URIManager> uriManagers, URIManager selectedURIManager) {
        this.generators = generators;
        this.stores = stores;
        this.uriManagers = uriManagers;
        this.selectedURIManager = selectedURIManager;
    }

    public Collection<URIManager> getURIManagers() {
        return Collections.unmodifiableCollection(uriManagers.values());
    }

    public URIManager getURIManager(String name) {
        return uriManagers.get(name);
    }

    public URIManager getSelectedURIManager() {
        return selectedURIManager;
    }

    public URIStore getURIStore(String name) {
        return stores.get(name);
    }

    public StringGenerator getGenerator(String name) {
        return generators.get(name);
    }

}
