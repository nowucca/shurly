/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurely.core.impl;

import com.nowucca.shurely.core.UniqueStringGenerator;

import java.util.concurrent.atomic.AtomicInteger;

public class Base32StringGenerator implements UniqueStringGenerator {


    private static final String ALPHABET = "abcdefhijkmnpqrtuvwxyz0123456789";
    private static final int BASE = ALPHABET.length();
    private static final AtomicInteger idGenerator = new AtomicInteger();


    public String get() {
        return encode(idGenerator.incrementAndGet());
    }

    String encode(int num) {
        StringBuilder sb = new StringBuilder();

        while (num > 0) {
            sb.append(ALPHABET.charAt(num % BASE));
            num /= BASE;
        }

        return sb.reverse().toString();
    }

    public int decode(String str) {
        int num = 0;

        for (int i = 0, len = str.length(); i < len; i++) {
            num = num * BASE + ALPHABET.indexOf(str.charAt(i));
        }

        return num;
    }
}
