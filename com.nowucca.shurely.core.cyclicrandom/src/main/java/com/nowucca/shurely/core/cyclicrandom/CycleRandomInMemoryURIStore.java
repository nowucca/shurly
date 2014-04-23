/**
 * Copyright (c) 2007-2012, Kaazing Corporation. All rights reserved.
 */
package com.nowucca.shurely.core.cyclicrandom;

import com.nowucca.shurely.core.URIStore;
import com.nowucca.shurely.core.AbstractInMemoryURIStore;
import com.nowucca.shurely.core.AbstractIntegerDrivenStringGenerator;

import javax.annotation.Resource;

public class CycleRandomInMemoryURIStore extends AbstractInMemoryURIStore implements URIStore {

    private CyclicRandomStringGenerator generator;

    @Resource
    public void setGenerator(CyclicRandomStringGenerator generator) {
        this.generator = generator;
    }

    @Override
    protected AbstractIntegerDrivenStringGenerator getStringGenerator() {
        return generator;
    }

    //-----------------------------------
    // test methods
    //-----------------------------------


    CyclicRandomStringGenerator getGenerator() {
        return generator;
    }
}
