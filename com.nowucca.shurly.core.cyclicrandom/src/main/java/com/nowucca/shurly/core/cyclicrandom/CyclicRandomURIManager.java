/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurly.core.cyclicrandom;

import com.nowucca.shurly.core.StringGenerator;
import com.nowucca.shurly.core.URIManager;
import com.nowucca.shurly.core.URIStore;
import com.nowucca.shurly.core.AbstractURIManager;

import javax.annotation.Resource;

public class CyclicRandomURIManager extends AbstractURIManager implements URIManager {

    private CyclicRandomStringGenerator generator;
    private CycleRandomInMemoryURIStore store;

    @Resource
    public void setGenerator(CyclicRandomStringGenerator generator) {
        this.generator = generator;
    }

    @Resource
    public void setStore(CycleRandomInMemoryURIStore store) {
        this.store = store;
    }

    @Override
    protected URIStore getURIStore() {
        return store;
    }

    @Override
    protected StringGenerator getStringGenerator() {
        return generator;
    }

    //---------------------------------------------------
    // test methods
    //---------------------------------------------------

    CyclicRandomStringGenerator getGenerator() {
        return generator;
    }

    CycleRandomInMemoryURIStore getStore() {
        return store;
    }

}
