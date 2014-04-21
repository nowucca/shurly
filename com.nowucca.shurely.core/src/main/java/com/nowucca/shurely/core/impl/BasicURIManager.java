/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurely.core.impl;

import com.nowucca.shurely.core.URIManager;
import com.nowucca.shurely.core.URIStore;
import com.nowucca.shurely.core.UniqueStringGenerator;

import java.net.URI;
import java.net.URISyntaxException;

public class BasicURIManager implements URIManager {
    
    UniqueStringGenerator generator = new SequentialAlphabetStringGenerator();

    URIStore store = new BidiURIMap();

    public static final String DOMAIN="shure.ly";

    public BasicURIManager() {
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
