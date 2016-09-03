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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.keydap.sparrow.PatchRequest.PatchOperation;

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
    
    @Test
    public void testSerializePatchReq() {
        PatchRequest pr = new PatchRequest("", PatchRequest.class);
        JsonObject members = new JsonObject();
        members.addProperty("ref", "abc");
        pr.add("members", members);
        
        String json = serializer.toJson(pr);
        
        // check that schemas field is present
        JsonObject jObj = (JsonObject) new JsonParser().parse(json);
        assertNotNull(jObj.get("schemas").getAsJsonArray());
        
        PatchRequest deserPr = serializer.fromJson(json, PatchRequest.class);
        assertEquals(1, deserPr.getOperations().size());
        
        PatchOperation po = deserPr.getOperations().get(0);
        assertNotNull(po);
        assertEquals("abc", ((JsonObject)po.getValue()).get("ref").getAsString());
        
        pr.remove("meta");
        pr.replace("members", members);
        
        json = serializer.toJson(pr);
        deserPr = serializer.fromJson(json, PatchRequest.class);
        assertEquals(3, deserPr.getOperations().size());
    }
}
