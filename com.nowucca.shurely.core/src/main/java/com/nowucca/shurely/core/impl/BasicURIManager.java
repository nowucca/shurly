/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurely.core.impl;

import com.nowucca.shurely.core.URIManager;
import com.nowucca.shurely.core.URIStore;
import com.nowucca.shurely.core.UniqueStringGenerator;

import javax.annotation.Resource;
import java.net.URI;
import java.net.URISyntaxException;

public class BasicURIManager implements URIManager {

    private static final String DOMAIN="shure.ly";

    static final String NAME = BasicURIManager.class.getCanonicalName();

    private SequentialAlphabetStringGenerator generator;

    private BidiURIMap store;

    @Resource
    public void setGenerator(SequentialAlphabetStringGenerator generator) {
        this.generator = generator;
    }

    @Resource
    public void setStore(BidiURIMap store) {
        this.store = store;
    }

    public String getName() {
        return NAME;
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

    //---------------------------
    // test methods
    //---------------------------


    SequentialAlphabetStringGenerator getGenerator() {
        return generator;
    }

    BidiURIMap getStore() {
        return store;
    }
}
