/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurly.core;

import java.net.URI;

/**
 * Basic Management operations on URI objects.
 */
public interface URIManager extends NamedObject {

    /**
     * Obtain the shortened version of a URI by "shrinking" it.
     * @param longURI the URI to be shortened
     * @return the shortened URI
     */
    public URI shrink(URI longURI);

    /**
     * Obtain the long version of a shortened URI by "following" the shortened
     * URI.
     *
     * @param shortURI the shortened URI
     * @return the long form of the shortened URI or {@code null} if none was found
     */
    public URI follow(URI shortURI);


    /**
     * A unique name identifying this kind of URI manager.
     * @return a unique name identifying this kind of uri manager.
     */
    String getName();

}
