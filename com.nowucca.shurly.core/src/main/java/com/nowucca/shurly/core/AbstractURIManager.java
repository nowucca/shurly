/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurly.core;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Captures the abstract behavior of the main methods {@link #shrink(java.net.URI)} and {@link #follow(java.net.URI)},
 * abstracting the notions of the {@link URIStore} and {@link StringGenerator} used.
 */
public abstract class AbstractURIManager extends AbstractLoadableEntity implements URIManager {

    public static final String DEFAULT_DOMAIN = "shure.ly";

    protected AbstractURIManager() {
    }

    protected abstract URIStore getURIStore();

    protected abstract StringGenerator getStringGenerator();

    public String getName() {
        return this.getClass().getCanonicalName();
    }

    public URI shrink(URI longURI) {
        if (longURI == null) {
            throw new NullPointerException("longURI");
        }


        URI shortURI = getURIStore().getShortURI(longURI);
        if (shortURI == null) {
            do {
                shortURI = makeShortening(longURI);
                URI existingShortURI = getURIStore().putIfAbsent(shortURI, longURI);
            } while (getURIStore().)
        }
        return shortURI;

        URI shortURI;
        URI existing = ;
        AtomicReference<URI> e = new AtomicReference<URI>(getURIStore().hasShortURI(longURI));
        while (e.compareAndSet(null, makeShortening(longURI))) {
            existing = getURIStore().putIfAbsent(e.get(),  longURI);
            if (existing != null) {
                e.set(existing);
            }
        }


        URI shrunk =  makeShortening(longURI);
        final URI existing = getURIStore().putIfAbsent(longURI, shrunk);
        if (existing != null) {
            shrunk = existing;
        }
        return shrunk;
    }

    public URI follow(URI shortURI) {
        if (shortURI == null) {
            throw new NullPointerException("shortURI");
        }
        return getURIStore().get(shortURI);
    }

    protected URI makeShortening(URI sourceURI) {
        try {
            return new URI(sourceURI.getScheme(),
                    getShorteningDomain(),
                    "/" + getStringGenerator().getString(),
                    null);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to shorten URI: " + sourceURI, e);
        }
    }

    private String getShorteningDomain() {
        return config.getString("domain", DEFAULT_DOMAIN);
    }
}
