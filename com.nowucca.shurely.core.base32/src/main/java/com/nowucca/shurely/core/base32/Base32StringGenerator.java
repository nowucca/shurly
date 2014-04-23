/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurely.core.base32;

import com.nowucca.shurely.core.IntegerDrivenStringGenerator;
import com.nowucca.shurely.core.AbstractIntegerDrivenStringGenerator;

import java.util.concurrent.atomic.AtomicInteger;

public class Base32StringGenerator extends AbstractIntegerDrivenStringGenerator implements IntegerDrivenStringGenerator {

    private static final String ALPHABET = "abcdefhijkmnpqrtuvwxyz0123456789";
    private static final int BASE = ALPHABET.length();

    private static final AtomicInteger idGenerator = new AtomicInteger();

    private static final String NAME = Base32StringGenerator.class.getCanonicalName();

    public String getString() {
        return encode(idGenerator.incrementAndGet());
    }

    @Override
    public String getAlphabet() {
        return ALPHABET;
    }
}
