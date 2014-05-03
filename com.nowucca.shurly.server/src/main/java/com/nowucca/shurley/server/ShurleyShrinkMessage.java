/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurley.server;

import java.net.URI;

public class ShurleyShrinkMessage extends ShurleyMessage {
    @Override
    public Kind getKind() {
        return Kind.SHRINK;
    }

    private URI longURI;

    public ShurleyShrinkMessage(short version, long msgId, URI longURI) {
        super(version, msgId);
        this.longURI = longURI;
    }

    public URI getLongURI() {
        return longURI;
    }
}
