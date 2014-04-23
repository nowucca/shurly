/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurely.core.base32;

import com.nowucca.shurely.core.StringGenerator;
import com.nowucca.shurely.core.URIManager;
import com.nowucca.shurely.core.URIStore;
import com.nowucca.shurely.core.AbstractURIManager;

import javax.annotation.Resource;

public class Base32URIManager extends AbstractURIManager implements URIManager {

    static final String NAME = Base32URIManager.class.getCanonicalName();

    private Base32StringGenerator generator;
    private Base32InMemoryURIStore store;

    @Resource
    public void setGenerator(Base32StringGenerator generator) {
        this.generator = generator;
    }

    @Resource
    public void setStore(Base32InMemoryURIStore store) {
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

    Base32StringGenerator getGenerator() {
        return generator;
    }

    Base32InMemoryURIStore getStore() {
        return store;
    }

}
