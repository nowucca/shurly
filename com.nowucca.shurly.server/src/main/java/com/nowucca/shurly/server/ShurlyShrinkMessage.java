/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurly.server;

import java.net.URI;

public class ShurlyShrinkMessage extends ShurlyMessage {
    @Override
    public Kind getKind() {
        return Kind.SHRINK;
    }

    private URI longURI;

    public ShurlyShrinkMessage(short version, long msgId, URI longURI) {
        super(version, msgId);
        this.longURI = longURI;
    }

    public URI getLongURI() {
        return longURI;
    }
}
