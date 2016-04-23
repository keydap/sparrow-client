/*
 * Copyright (c) 2016 Keydap Software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * See LICENSE file for details.
 */
package com.keydap.sparrow;

import org.junit.Test;
import static org.junit.Assert.*;

import com.google.gson.Gson;

/**
 *
 * @author Kiran Ayyagari (kayyagari@keydap.com)
 */
public class SerilizationTest {
    private static final Gson serializer = new Gson();

    @Test
    public void testErrorSerialization() {
        String json = "{\"schemas\":[\"urn:ietf:params:scim:api:messages:2.0:Error\"],\"scimType\":\"mutability\",\"detail\":\"Attribute 'id' is readOnly\",\"status\":\"400\"}";
        Error e = serializer.fromJson(json, Error.class);
        assertNotNull(e);
        assertNotNull(e.getDetail());
        assertNotNull(e.getSchemas());
        assertNotNull(e.getScimType());
        assertNotNull(e.getStatus());
    }
}
