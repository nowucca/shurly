/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurly.server;

public class ShurlyErrorMessage extends ShurlyMessage {
    @Override
    public Kind getKind() {
        return Kind.ERROR;
    }

    private long errorCode;
    private String reason;

    public ShurlyErrorMessage(short version, long msgId, long errorCode, String reason) {
            super(version, msgId);
            this.errorCode = errorCode;
            this.reason = reason;
        }

    public ShurlyErrorMessage(short version, long msgId, ShurlyErrorCode code) {
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
