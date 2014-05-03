/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurley.server;

public class ShurleyErrorMessage extends ShurleyMessage {
    @Override
    public Kind getKind() {
        return Kind.ERROR;
    }

    private long errorCode;
    private String reason;

    public ShurleyErrorMessage(short version, long msgId, long errorCode, String reason) {
            super(version, msgId);
            this.errorCode = errorCode;
            this.reason = reason;
        }

    public ShurleyErrorMessage(short version, long msgId, ShurleyErrorCode code) {
        super(version, msgId);
        this.errorCode = code.getCode();
        this.reason = code.getReason();
    }

    public long getErrorCode() {
        return errorCode;
    }

    public String getReason() {
        return reason;
    }
}
