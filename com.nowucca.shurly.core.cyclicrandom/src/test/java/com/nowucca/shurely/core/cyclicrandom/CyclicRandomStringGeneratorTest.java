/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurely.core.cyclicrandom;

import com.nowucca.shurely.core.cyclicrandom.CyclicRandomStringGenerator;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

public class CyclicRandomStringGeneratorTest {

    private CyclicRandomStringGenerator generator;

    @Before
    public void setUp() throws Exception {
        generator = new CyclicRandomStringGenerator();
    }

    @Test
    public void shouldStartWithPredictableEncodings() throws Exception {
        for (int i = 1; i < 6; i++) {
            // System.out.format("%d: %s\n", i, generator.getString());
            switch (i) {
                case 1:
                    Assert.assertEquals("dRUPyN", generator.getString());
                    break;
                case 2:
                    Assert.assertEquals("1HqrU", generator.getString());
                    break;
                case 3:
                case 1003:
                    Assert.assertEquals("cn1mT4", generator.getString());
                    break;

                case 4:
                    Assert.assertEquals("bNrDRd", generator.getString());
                    break;

                case 5:
                    Assert.assertEquals("e9P1uR", generator.getString());
                    break;
            }
        }
    }

    @Test
    public void shouldCycleWithPredictableEncodings() throws Exception {
        for (int i = 1; i < 1006; i++) {
            switch (i) {
                case 1:
                case 1001:
                    Assert.assertEquals("dRUPyN", generator.getString());
                    break;
                case 2:
                case 1002:
                    Assert.assertEquals("1HqrU", generator.getString());
                    break;

                case 3:
                case 1003:
                    Assert.assertEquals("cn1mT4", generator.getString());
                    break;

                case 4:
                case 1004:
                    Assert.assertEquals("bNrDRd", generator.getString());
                    break;

                case 5:
                case 1005:
                    Assert.assertEquals("e9P1uR", generator.getString());
                    break;

                default:
                    generator.getString();
            }
        }
    }


}
