/*
 * Copyright (c) 2016 Keydap Software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * See LICENSE file for details.
 */
package com.keydap.sparrow;

/**
 *
 * @author Kiran Ayyagari (kayyagari@keydap.com)
 */
public class Error {
    private String[] schemas;
    private String scimType;
    private String detail;
    private String status;

    public String[] getSchemas() {
        return schemas;
    }

    public String getScimType() {
        return scimType;
    }

    public String getDetail() {
        return detail;
    }

    public String getStatus() {
        return status;
    }

    /*default protection*/ void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "Error [scimType=" + scimType + ", detail=" + detail
                + ", status=" + status + "]";
    }
}
