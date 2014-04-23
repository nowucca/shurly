/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurely.core.basic;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BasicSequentialAlphabetStringGeneratorTest {

    private static final int ALPHABET_LENGTH = new BasicSequentialAlphabetStringGenerator().alphabet.length;
    BasicSequentialAlphabetStringGenerator generator;

    byte[] alphabet;

    @Before
    public void before() {
        generator = new BasicSequentialAlphabetStringGenerator();
        alphabet = generator.alphabet;
    }

    @Test
    public void defaultShouldHaveNextElement() throws Exception {
        Assert.assertTrue(generator.hasNext());
    }

    @Test
    public void shouldYieldCorrectFirstElement() throws Exception {
        Assert.assertEquals(String.valueOf((char)alphabet[0]), generator.getString());
    }

    @Test
    public void shouldYieldCorrectSecondElement() throws Exception {
        Assert.assertEquals(String.valueOf((char)alphabet[0]), (generator.getString()));
        Assert.assertEquals(String.valueOf((char)alphabet[1]), (generator.getString()));
    }

    @Test
    public void shouldYieldCorrectFinalAlphabetElement() throws Exception {
        String result = null;
        for(int i = 0; i< ALPHABET_LENGTH; i++) {
            result = generator.getString();
        }
        Assert.assertEquals(String.valueOf((char)alphabet[ALPHABET_LENGTH-1]), (result));

    }

    @Test
    public void shouldExpandLengthOfStringFromOneToTwoAsExpected() throws Exception {
        String result = null;
        for(int i = 0; i<ALPHABET_LENGTH+1; i++) {
            result = generator.getString();
        }
        Assert.assertEquals(new String(new byte[]{alphabet[0],alphabet[0]}),(result));

    }

    @Test
    public void shouldIncrementMostSignificantDigitAsExpectedForLength2Strings() throws Exception {
        String result = null;
        for(int i = 0; i<2* ALPHABET_LENGTH +1; i++) {
            result = generator.getString();
        }
        Assert.assertEquals(new String(new byte[]{alphabet[1],alphabet[0]}),(result));

    }

    @Test
    public void shouldBeAsExpectedForRandomValueOfLength2() throws Exception {
        String result = null;
        for(int i = 0; i<2* ALPHABET_LENGTH +1+5; i++) {
            result = generator.getString();
        }
        Assert.assertEquals(new String(new byte[]{alphabet[1],alphabet[5]}),(result));

    }


    int getCallCountForStringLength(int length) {
        int calls = 0;
        if ( length <= 0 ) return 0;

        for(int i = 0; i<length; i++) {
            calls += ((int)Math.pow(ALPHABET_LENGTH, i));
        }
        return calls;
    }

    @Test
    public void shouldFlipToAllFreshCharactersWhenIncreasingLength() throws Exception {

        for (int length = 1; length<6; length++) {

            generator = new BasicSequentialAlphabetStringGenerator();
            String result=null;
            int count = getCallCountForStringLength(length);

            for(int i = 0; i<count; i++) {
                result = generator.getString();
            }


            Assert.assertEquals(getExpectedString(length),(result));
        }

    }

    private String getExpectedString(int length) {
        String result = "";
        for ( int i = 0; i < length; i++) {
            result +=String.valueOf((char)alphabet[0]);
        }
        return result;
    }

    @Test 
    public void demonstrateFirst1000() throws Exception {
        for(int i = 0; i<1000; i++) {
            System.out.println(i+": '"+ generator.getString()+"'");
        }

    }
}
