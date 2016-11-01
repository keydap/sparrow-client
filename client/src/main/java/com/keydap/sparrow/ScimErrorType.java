/*
 * Copyright (c) 2016 Keydap Software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * See LICENSE file for details.
 */
package com.keydap.sparrow;

/**
 * SCIM v2 error types
 * 
 * @author Kiran Ayyagari (kayyagari@keydap.com)
 */
public enum ScimErrorType {
    INVALIDFILTER("invalidFilter"),
    TOOMANY("tooMany"),
    UNIQUENESS("uniqueness"),
    MUTABILITY("mutability"),
    INVALIDSYNTAX("invalidSyntax"),
    INVALIDPATH("invalidPath"),
    NOTARGET("noTarget"),
    INVALIDVALUE("invalidValue"),
    INVALIDVERS("invalidVers"),
    SENSITIVE("sensitive");

    private String value;
    
    private ScimErrorType(String value) {
        this.value = value;
    }
    
    public String value() {
        return value;
    }
}
