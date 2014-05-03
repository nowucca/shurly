/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurly.core;

/**
 * A string generator is a provider of strings, typically for
 * use when constructing new short URIs.
 *
 */
public interface StringGenerator extends NamedObject {

    /**
     * Obtain a string value
     * @return a string
     */
    String getString();

}
