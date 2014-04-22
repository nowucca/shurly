/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurely.core.basic;

import com.nowucca.shurely.core.URIStore;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;

public class BasicInMemoryURIStore implements URIStore {


    private static final String NAME = BasicInMemoryURIStore.class.getCanonicalName();

    public String getName() {
        return NAME;
    }

        ConcurrentHashMap<URI, URI> long2short = new ConcurrentHashMap<URI, URI>();
        ConcurrentHashMap<URI, URI> short2long = new ConcurrentHashMap<URI, URI>();

        public synchronized URI putIfAbsent(URI longURI, URI shortURI) {
            URI existing = long2short.putIfAbsent(longURI, shortURI);
            if ( existing != null ) {
                return existing;
            } else {
                existing = short2long.putIfAbsent(shortURI, longURI);
                if ( existing != null ) {
                    throw new IllegalStateException(
                            String.format("Attempt to establish shortened URI '%s' for '%s' when it shortens '%s' already.",
                                    shortURI, longURI, existing));
                }
            }
            return null;
        }

        public synchronized URI get(URI shortURI) {
            return short2long.get(shortURI);
        }



    }
