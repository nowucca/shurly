/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurley.server;

import java.net.URI;

public class ShurleyShrunkMessage extends ShurleyShrinkMessage {

    @Override
    public Kind getKind() {
        return Kind.SHRUNK;
    }

    private URI shortURI;

    public ShurleyShrunkMessage(short version, long msgId, URI longURI, URI shortURI) {
        super(version, msgId, longURI);
        this.shortURI = shortURI;
    }

    public URI getShortURI() {
        return shortURI;
    }
}
