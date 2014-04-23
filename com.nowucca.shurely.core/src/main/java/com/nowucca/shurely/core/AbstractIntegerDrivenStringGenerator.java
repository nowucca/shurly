/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurely.core;

import com.nowucca.shurely.core.IntegerDrivenStringGenerator;

public abstract class AbstractIntegerDrivenStringGenerator implements IntegerDrivenStringGenerator {

    public String getName() {
        return this.getClass().getCanonicalName();
    }

    /**
     * What alphabet are you using to map integers onto?
     *
     * @return a string where the set of chars is equivalent to the alphabet to be used for encoding/decoding.
     */
    protected abstract String getAlphabet();

    public String encode(int num) {
        StringBuilder sb = new StringBuilder();

        while (num > 0) {
            sb.append(getAlphabet().charAt(num % getAlphabet().length()));
            num /= getAlphabet().length();
        }

        return sb.reverse().toString();
    }

    public int decode(String str) {
        int num = 0;

        for (int i = 0, len = str.length(); i < len; i++) {
            num = num * getAlphabet().length() + getAlphabet().indexOf(str.charAt(i));
        }

        return num;
    }
}
