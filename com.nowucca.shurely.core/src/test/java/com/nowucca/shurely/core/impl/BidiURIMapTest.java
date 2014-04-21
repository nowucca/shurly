/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurely.core.impl;

import com.nowucca.shurely.core.URIStore;
import com.nowucca.shurely.core.impl.BidiURIMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

public class BidiURIMapTest {

    private URIStore map;

    @Before
    public void before() throws Exception {
        map = new BidiURIMap();
    }

    @Test
    public void shouldPutAndRetrieveASingleKeyValuePair() throws Exception {
        final URI longURI = URI.create("http://www.google.com");
        final URI shortURI = URI.create("http://shure.ly/aa");
        URI existing = map.putIfAbsent(longURI, shortURI);
        Assert.assertNull(existing);
        Assert.assertSame(longURI, map.get(shortURI));
    }

    @Test
    public void shouldReturnExistingShortenedURIIfPresent() throws Exception {
        final URI longURI = URI.create("http://www.google.com");
        final URI shortURI = URI.create("http://shure.ly/aa");
        final URI alternateShortURI = URI.create("http://shure.ly/bb");
        URI existing = map.putIfAbsent(longURI, shortURI);
        Assert.assertNull(existing);
        existing = map.putIfAbsent(longURI, alternateShortURI);
        Assert.assertNotNull(existing);
        Assert.assertEquals("Expected the first shortened URI for the same long URI to be returned",
                shortURI, existing);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotBeAbleToClaimAShortenedURIForMultipleLongURIs() throws Exception {
        final URI longURI = URI.create("http://www.google.com");
        final URI shortURI = URI.create("http://shure.ly/aa");
        final URI alternateLongURI = URI.create("http://www.yahoo.com");


        URI existing = map.putIfAbsent(longURI, shortURI);
        Assert.assertNull(existing);
        map.putIfAbsent(alternateLongURI, shortURI);
    }
}
