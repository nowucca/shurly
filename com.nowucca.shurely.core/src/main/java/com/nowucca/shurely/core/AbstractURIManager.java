/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurely.core;

import com.nowucca.shurely.core.StringGenerator;
import com.nowucca.shurely.core.URIManager;
import com.nowucca.shurely.core.URIStore;

import java.net.URI;
import java.net.URISyntaxException;

public abstract class AbstractURIManager implements URIManager {

    public static final String DOMAIN="shure.ly";

    protected abstract URIStore getURIStore();

    protected abstract StringGenerator getStringGenerator();

    public String getName() {
        return this.getClass().getCanonicalName();
    }

    public URI shrink(URI longURI) {
        if ( longURI == null ) {
            throw new NullPointerException("longURI");
        }
        URI shrunk =  makeShortening(longURI);
        URI existing = getURIStore().putIfAbsent(longURI, shrunk);
        if ( existing != null ) {
            shrunk = existing;
        }
        return shrunk;
    }

    public URI follow(URI shortURI) {
        if ( shortURI == null ) {
            throw new NullPointerException("shortURI");
        }
        return getURIStore().get(shortURI);
    }

    protected URI makeShortening(URI sourceURI) {
        try {
            return new URI(sourceURI.getScheme(), DOMAIN, "/"+getStringGenerator().getString(), null);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to shorten URI: "+sourceURI, e);
        }
    }
}
