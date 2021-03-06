/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurly.core.basic;

import com.nowucca.shurly.core.AbstractLoadableEntity;
import com.nowucca.shurly.core.StringGenerator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BasicSequentialAlphabetStringGenerator
        extends AbstractLoadableEntity
        implements Iterator<String>, StringGenerator {

    private int currentLength;

    static final byte[] DEFAULT_ALPHABET = new byte[] {
              'a', 'b', 'c', 'd', 'e', 'f'};

    final byte[] alphabet;

    private List<Integer> positions = new ArrayList<Integer>();
    /*
    /a /b /c ... /f
    /aa /ab ... /af
    /ba /bb ... /bf
    ...
    /za /zb ... /zz

    /aaa ... /zzz
     */

    public BasicSequentialAlphabetStringGenerator() {
        super();
        currentLength++;
        positions.add(0);
        alphabet = config.getByteArray("alphabet", DEFAULT_ALPHABET);
    }

    public String getString() {
        return next();
    }

    public boolean hasNext() {
        return true;
    }

    public String next() {
        final int length = currentLength;

        // Loop over characters in string so far right to left
        for (int j = 0; j <= length - 1; j++) {

                // If we ran out of characters for this digit...
                if (positions.get(length - 1 - j) == alphabet.length) {

                    // If you are the most significant digit, we are
                    // totally out of digits, so add a digit and
                    // reset all digits to the starting character...
                    if (j == length - 1) {
                        currentLength++;
                        positions.add(0);
                        for (int jj = 0; jj < j ; jj++) {
                            positions.set(length - 1 - jj, 0);
                        }
                    }

                    // If you are not the most significant digit, and we've run out
                    // increment the character in the slot before you.
                    if (j < length - 1) {
                        positions.set(length - 1 - j - 1, positions.get(length - 1 - j - 1) + 1);
                    }

                    // We've run out of characters for this slot, reset it to
                    // the first character in the alphabet.
                    positions.set(length - 1 - j , 0);
                }
        }

        // Build the result right to left, keyed by the indices into
        // the alphabet in the positions array.
        final char[] result = new char[currentLength];
        for (int i = 0; i <= currentLength - 1; i++) {
            final char c = (char) alphabet[positions.get(currentLength - 1 - i)];
            result[currentLength - 1 - i] = c;
        }

        // We've consumed the last character, so increment it for the current digit.
        positions.set(currentLength - 1,  positions.get(currentLength - 1) + 1);

        return String.valueOf(result);
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public String getName() {
        return this.getClass().getCanonicalName();
    }
}
