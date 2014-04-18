/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurley.server;

public abstract class ShurleyMessage {
    public static enum Kind { SHRINK(1), SHRUNK(2), ERROR(3), FOLLOW(4);

        private int value;

        private Kind(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public abstract Kind getKind();

    private short version;
    private long msgId;

    public ShurleyMessage(short version, long msgId) {
        this.version = version;
        this.msgId = msgId;
    }

    public short getVersion() {
        return version;
    }

    public long getMsgId() {
        return msgId;
    }

}
