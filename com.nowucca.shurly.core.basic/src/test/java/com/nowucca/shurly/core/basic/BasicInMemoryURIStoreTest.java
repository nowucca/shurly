/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurly.core.basic;

import com.nowucca.shurly.core.URIStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

public class BasicInMemoryURIStoreTest {

    private URIStore map;

    @Before
    public void before() throws Exception {
        map = new BasicInMemoryURIStore();
    }

    @Test
    public void shouldPutAndRetrieveASingleKeyValuePair() throws Exception {
        final URI longURI = URI.create("http://www.google.com");
        final URI shortURI = URI.create("http://shure.ly/aa");
        URI existing = map.putIfAbsent(longURI, shortURI);
        Assert.assertNull(existing);
        Assert.assertSame(longURI, map.retrieve(shortURI));
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
