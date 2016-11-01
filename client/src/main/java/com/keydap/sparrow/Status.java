/*
 * Copyright (c) 2016 Keydap Software.
 *
 * Licensed under the Apache License, Version 2.0 (the License);
 * See LICENSE file for complete details.
 */
package com.keydap.sparrow;

/**
 * Enumeration of various SCIM v2 error codes
 * 
 * @author Kiran Ayyagari (kayyagari@keydap.com)
 */
public enum Status {
    TempRedirect("307"),
    PermRedirect("308"),
    BadRequest("400"),
    UnAuthorized("401"),
    Forbidden("403"),
    NotFound("404"),
    Conflict("409"),
    PreCondFailed("412"),
    PayloadTooLarge("413"),
    InternalServerErr("500"),
    NotImplemented("501"),
    UNKNOWN("-1");

    private String value;
    
    private Status(String code) {
        this.value = code;
    }
    
    public static Status byCode(int code) {
        return byValue(String.valueOf(code));
    }
    
    public static Status byValue(String code) {
        for(Status s : values()) {
            if (s.value.equals(code)) {
                return s;
            }
        }
        
        return UNKNOWN;
    }

    public String value() {
        return value;
    }

}
