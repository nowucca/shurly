/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurly.core.base32;

import com.nowucca.shurly.core.AbstractInMemoryURIStore;
import com.nowucca.shurly.core.AbstractIntegerDrivenStringGenerator;

import javax.annotation.Resource;

public class Base32InMemoryURIStore extends AbstractInMemoryURIStore {

    private Base32StringGenerator generator;

    @Resource
    public void setGenerator(Base32StringGenerator generator) {
        this.generator = generator;
    }

    @Override
    protected AbstractIntegerDrivenStringGenerator getStringGenerator() {
        return generator;
    }

    //-----------------------------------
    // test methods
    //-----------------------------------


    Base32StringGenerator getGenerator() {
        return generator;
    }
}
