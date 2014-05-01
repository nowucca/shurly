/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurly.core;

import java.net.URI;
import java.net.URISyntaxException;

public abstract class AbstractURIManager extends AbstractLoadableEntity implements URIManager {

    public static final String DEFAULT_DOMAIN="shure.ly";

    protected AbstractURIManager() {
    }

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
            return new URI(sourceURI.getScheme(),
                    getShorteningDomain(),
                    "/"+getStringGenerator().getString(),
                    null);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to shorten URI: "+sourceURI, e);
        }
    }

    private String getShorteningDomain() {
        return config.getString("domain", DEFAULT_DOMAIN);
    }
}
