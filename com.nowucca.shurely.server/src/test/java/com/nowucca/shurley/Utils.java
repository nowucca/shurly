/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurley;

import java.nio.charset.Charset;

public class Utils {

    private static final byte[] EMPTY_BYTES = new byte[0];

    public static byte[][] stringsToByteArrays(String... strings) {
        return stringsToByteArrays(Charset.forName("UTF-8"), strings);
    }

    public static byte[][] stringsToByteArrays(Charset charset, String... strings) {
        if (strings == null ) {
            throw new NullPointerException("strings");
        }

        byte[][] result = new byte[strings.length][];
        if (strings.length == 0) {
            return result;
        }

        int i = 0;
        for (String s : strings) {
            if (s != null) {
                result[i] = s.getBytes(charset);
            } else {
                result[i] = EMPTY_BYTES;
            }
            i++;
        }
        return result;
    }
}
