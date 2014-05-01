/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurley.server;

public enum ShurleyErrorCode {

    UNKNOWN_ERROR(1L, "An unknown error has occurred.");

    private long code;
    private String reason;

    private ShurleyErrorCode(long code, String reason) {
        this.code = code;
        this.reason = reason;
    }

    public long getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }
}
