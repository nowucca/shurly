/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurly.core.base32;

import com.nowucca.shurly.core.AbstractIntegerDrivenStringGenerator;
import com.nowucca.shurly.core.IntegerDrivenStringGenerator;

import java.util.concurrent.atomic.AtomicInteger;

public class Base32StringGenerator extends AbstractIntegerDrivenStringGenerator implements IntegerDrivenStringGenerator {

    private static final String DEFAULT_ALPHABET = "abcdefhijkmnpqrtuvwxyz0123456789";
    private static final AtomicInteger idGenerator = new AtomicInteger();

    private final String alphabet;

    public Base32StringGenerator() {
        super();
        this.alphabet = config.getString("alphabet", DEFAULT_ALPHABET);
    }

    public String getString() {
        return encode(idGenerator.incrementAndGet());
    }

    @Override
    public String getAlphabet() {
        return alphabet;
    }
}
