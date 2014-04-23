/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurely.core.cyclicrandom;

import com.nowucca.shurely.core.IntegerDrivenStringGenerator;
import com.nowucca.shurely.core.AbstractIntegerDrivenStringGenerator;

public class CyclicRandomStringGenerator extends AbstractIntegerDrivenStringGenerator implements IntegerDrivenStringGenerator {

    private static final String ALPHABET = "abcdefhijkmnpqrtuvwxyz0123456789ABCDEFHUJKMNPQRTUVWXYZ"; //54
    private static final int BASE = ALPHABET.length();

    private CyclicRandom idGenerator;


    public CyclicRandomStringGenerator() {
        idGenerator = new CyclicRandom(4212521L, 1000); //TODO should be configured
    }

    public String getString() {
        return encode(idGenerator.nextInt());
    }

    @Override
    public String getAlphabet() {
        return ALPHABET;
    }
}
