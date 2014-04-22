/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurely.core.base32;

import com.nowucca.shurely.core.URIManager;

import javax.annotation.Resource;
import java.net.URI;
import java.net.URISyntaxException;

public class Base32URIManager implements URIManager {

    public static final String DOMAIN="shure.ly";

    static final String NAME = Base32URIManager.class.getCanonicalName();


    private Base32StringGenerator generator;
    private Base32InMemoryURIStore store;

    @Resource
    public void setGenerator(Base32StringGenerator generator) {
        this.generator = generator;
    }

    @Resource
    public void setStore(Base32InMemoryURIStore store) {
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

    //---------------------------------------------------
    // test methods
    //---------------------------------------------------

    Base32StringGenerator getGenerator() {
        return generator;
    }

    Base32InMemoryURIStore getStore() {
        return store;
    }

}
