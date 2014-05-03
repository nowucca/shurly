/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurley.server;

import java.net.URI;

public class ShurleyFollowMessage extends ShurleyMessage {
    @Override
    public Kind getKind() {
        return Kind.FOLLOW;
    }

    private URI shortURI;

    public ShurleyFollowMessage(short version, long msgId, URI shortURI) {
        super(version, msgId);
        this.shortURI = shortURI;
    }

    public URI getShortURI() {
        return shortURI;
    }
}
