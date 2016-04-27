/*
 * Copyright (c) 2016 Keydap Software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * See LICENSE file for details.
 */
package com.keydap.sparrow;

import org.apache.http.Header;

/**
 *
 * @author Kiran Ayyagari (kayyagari@keydap.com)
 */
public class Response<T> {
    private T resource;
    private int httpCode;
    private Error error;
    private String httpBody;
    private Header[] headers;
    
    public T getResource() {
        return resource;
    }

    /*default protection*/ void setResource(T resource) {
        this.resource = resource;
    }

    public int getHttpCode() {
        return httpCode;
    }

    /*default protection*/void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public Error getError() {
        return error;
    }

    /*default protection*/ void setError(Error error) {
        this.error = error;
    }

    public String getHttpBody() {
        return httpBody;
    }

    /*default protection*/ void setHttpBody(String httpBody) {
        this.httpBody = httpBody;
    }
    
    /*default protection*/ void setHeaders(Header[] headers) {
        this.headers = headers;
    }

    public Header[] getHeaders() {
        return headers;
    }

    public String getLocation() {
        for(Header h : headers) {
            if(h.getName().equals("Location")) {
                return h.getValue();
            }
        }
        
        return null;
    }
}
