/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurly.core.cyclicrandom;

import com.nowucca.shurly.core.IntegerDrivenStringGenerator;
import com.nowucca.shurly.core.AbstractIntegerDrivenStringGenerator;

public class CyclicRandomStringGenerator
        extends AbstractIntegerDrivenStringGenerator
        implements IntegerDrivenStringGenerator {

    private static final String DEFAULT_ALPHABET =
            "abcdefhijkmnpqrtuvwxyz0123456789";

    private static final long DEFAULT_SEED = 4212521L;
    private static final int DEFAULT_PERIOD = 1000;

    private final String alphabet;
    private final CyclicRandom idGenerator;


    public CyclicRandomStringGenerator() {
        alphabet = config.getString("alphabet", DEFAULT_ALPHABET);

        long seed = config.getLong("seed", DEFAULT_SEED);
        int period = config.getInteger("period", DEFAULT_PERIOD);
        idGenerator = new CyclicRandom(seed, period);
    }

    public String getString() {
        return encode(idGenerator.nextInt());
    }

    @Override
    public String getAlphabet() {
        return alphabet;
    }
}
