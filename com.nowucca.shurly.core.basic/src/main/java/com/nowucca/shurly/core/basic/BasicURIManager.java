/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurly.core.basic;

import com.nowucca.shurly.core.StringGenerator;
import com.nowucca.shurly.core.URIManager;
import com.nowucca.shurly.core.URIStore;
import com.nowucca.shurly.core.AbstractURIManager;

import javax.annotation.Resource;

public class BasicURIManager extends AbstractURIManager implements URIManager {

    private BasicSequentialAlphabetStringGenerator generator;

    private BasicInMemoryURIStore store;

    @Resource
    public void setGenerator(BasicSequentialAlphabetStringGenerator generator) {
        this.generator = generator;
    }

    @Resource
    public void setStore(BasicInMemoryURIStore store) {
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

    //---------------------------
    // test methods
    //---------------------------


    BasicSequentialAlphabetStringGenerator getGenerator() {
        return generator;
    }

    BasicInMemoryURIStore getStore() {
        return store;
    }
}
