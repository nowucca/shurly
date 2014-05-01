/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurly.core.cyclicrandom;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Math.abs;

public class CyclicRandom {

    private Random random;
    private long seed;
    private int period;
    private AtomicLong count;

    public CyclicRandom(long seed, int period) {
        random = new Random(seed);
        this.seed = seed;
        this.period = period;
        this.count = new AtomicLong(0L);
    }

    public int nextInt() {
        next();
        return abs(random.nextInt());
    }

    private void next() {
        count.incrementAndGet();
        if (count.get() > period) {
            random.setSeed(seed);
            count.set(0L);
        }
    }
}