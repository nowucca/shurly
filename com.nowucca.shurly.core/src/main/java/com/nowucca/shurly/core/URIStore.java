/**
 * Copyright (c) 2013-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurly.core;

import java.net.URI;

/**
 * A URIStore remembers the association between long and short URIs,
 * and can be asked for a long URI given a short URI.
 *
 * Conceptually a URIStore stores mappings from long to short URIs that can
 * be looked up in the reverse direction, from short to long URIs.
 *
 * @since 1.0
 */
public interface URIStore extends NamedObject {

    /**
     * <p>Stores and associated a long URI with a short URI.</p>
     *
     * <p>If the specified longURI is not already associated with a shortURI,
     * associate it with the given shortURI and return <code>null</code>,
     * otherwise return the existing associated short URI.</p>
     *
     *
     * @param longURI     a uri to be shortened
     * @param shortURI    the associated shortened uri
     * @return <code>null</code> if a new association with
     *         the given parameters is stored, otherwise return
     *         the existing shortURI for the longURI.
     */
    URI putIfAbsent(URI longURI, URI shortURI);

    /**
     * Find a long URI for a given short URI.
     * @param shortURI the short URI to use as a lookup key
     * @return the corresponding long URI, or <code>null</code> if
     *         no such long URI can be found.
     */
    URI get(URI shortURI);
}
