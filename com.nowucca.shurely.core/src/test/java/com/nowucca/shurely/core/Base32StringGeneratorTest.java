/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurely.core;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

public class Base32StringGeneratorTest {

    private Base32StringGenerator generator;

    @Before
    public void setUp() throws Exception {
        generator = new Base32StringGenerator();
    }

    @Test
    public void shouldStartAtId1WhenCallingGetInitially() throws Exception {
        Assert.assertEquals("b", generator.get());
        Assert.assertEquals("c", generator.get());
    }

    @Test
    public void shouldGenerateExpectedShortUrlsForBoundaryIds() throws Exception {
        Assert.assertEquals("", generator.encode(0));
        Assert.assertEquals("b", generator.encode(1));
        Assert.assertEquals("9", generator.encode(31));
        Assert.assertEquals("ba", generator.encode(32));
        Assert.assertEquals("b9", generator.encode(63));
        Assert.assertEquals("ca", generator.encode(64));
        Assert.assertEquals("da", generator.encode(96));
    }

    @Test
    public void shouldDecodeExpectedIdsForBoundaryUrls() throws Exception {
        Assert.assertEquals(0, generator.decode(""));
        Assert.assertEquals(1, generator.decode("b"));
        Assert.assertEquals(31, generator.decode("9"));
        Assert.assertEquals(32, generator.decode("ba"));
        Assert.assertEquals(63, generator.decode("b9"));
        Assert.assertEquals(64, generator.decode("ca"));
        Assert.assertEquals(96, generator.decode("da"));
    }


}
