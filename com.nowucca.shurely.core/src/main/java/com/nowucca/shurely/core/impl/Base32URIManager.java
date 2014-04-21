/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurely.core.impl;

import com.nowucca.shurely.core.IntegerDrivenStringGenerator;
import com.nowucca.shurely.core.URIManager;

import java.net.URI;
import java.net.URISyntaxException;

public class Base32URIManager implements URIManager {

    IntegerDrivenStringGenerator generator = new Base32StringGenerator();

    public static final String DOMAIN="shure.ly";

    private ConcurrentMapURIStore store;

    public Base32URIManager() {
        store = new ConcurrentMapURIStore(generator);
    }

    public URI shrink(URI longURI) {
        if ( longURI == null ) {
            throw new NullPointerException("longURI");
        }
        URI shrunk =  makeShortening(longURI);
        URI existing = store.putIfAbsent(longURI, shrunk);
        if ( existing != null ) {
            shrunk = existing;
        }
        return shrunk;
    }

    public URI follow(URI shortURI) {
        if ( shortURI == null ) {
            throw new NullPointerException("shortURI");
        }
        return store.get(shortURI);
    }

    private URI makeShortening(URI sourceURI) {
        try {
            return new URI(sourceURI.getScheme(), DOMAIN, "/"+generator.get(), null);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to shorten URI: "+sourceURI, e);
        }
    }


}
