/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurly.core.cyclicrandom;

import com.nowucca.shurly.core.URIStore;
import com.nowucca.shurly.core.AbstractInMemoryURIStore;
import com.nowucca.shurly.core.AbstractIntegerDrivenStringGenerator;

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
