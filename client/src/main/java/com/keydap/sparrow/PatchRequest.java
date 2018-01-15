/*
 * Copyright (c) 2016 Keydap Software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * See LICENSE file for details.
 */
package com.keydap.sparrow;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;

/**
 * SCIM v2 Patch request
 * 
 * @author Kiran Ayyagari (kayyagari@keydap.com)
 */
public class PatchRequest {
    private String[] schemas = {"urn:ietf:params:scim:api:messages:2.0:PatchOp"};

    @SerializedName("Operations")
    private List<PatchOperation> operations = new ArrayList<PatchOperation>();

    // the below fields are marked transient to avoid JSON serialization
    /** the class of the resource to be patched */
    private transient Class<?> resClass;
    
    /** the ID of the resource to be patched */
    private transient String id;

    /** the attributes to be returned */
    private transient String attributes;

    /** the value of the If-None-Match header */
    private transient String ifNoneMatch;

    private transient JsonParser parser = new JsonParser();
    
    class PatchOperation {
        private String op;
        private String path;
        private JsonElement value;

        public PatchOperation(String op, String path) {
            this(op, path, null);
        }

        public PatchOperation(String op, String path, JsonElement value) {
            this.op = op;
            this.path = path;
            this.value = value;
        }

        public String getOp() {
            return op;
        }

        public String getPath() {
            return path;
        }

        public JsonElement getValue() {
            return value;
        }

        /*default protection*/ void setPath(String path) {
            this.path = path;
        }
    }

    public PatchRequest(String id, Class<?> resClass) {
        this(id, resClass, null);
    }
    
    public PatchRequest(String id, Class<?> resClass, String ifNoneMatch) {
        this.id = id;
        this.resClass = resClass;
        this.ifNoneMatch = ifNoneMatch;
    }
    
    public String getId() {
        return id;
    }

    public Class<?> getResClass() {
        return resClass;
    }

    public String getAttributes() {
        return attributes;
    }

    public String getIfNoneMatch() {
        return ifNoneMatch;
    }

    public void setIfNoneMatch(String ifNoneMatch) {
        this.ifNoneMatch = ifNoneMatch;
    }

    public void setAttributes(String attributes) {
        if(attributes != null) {
            attributes = attributes.trim();
            if(attributes.length() == 0) {
                attributes = null;
            }
        }
        
        this.attributes = attributes;
    }

    public void add(JsonElement elm) {
        add(null, elm);
    }

    public void add(String json) {
        JsonElement elm = parser.parse(json);
        add(null, elm);
    }

    public void add(String path, String json) {
        JsonElement elm = parser.parse(json);
        add(path, elm);
    }

    public void add(String path, JsonElement elm) {
        operations.add(new PatchOperation("add", path, elm));
    }

    public void replace(JsonElement elm) {
        replace(null, elm);
    }

    public void replace(String json) {
        JsonElement elm = parser.parse(json);
        replace(null, elm);
    }

    public void replace(String path, String json) {
        JsonElement elm = parser.parse(json);
        replace(path, elm);
    }

    public void replace(String path, JsonElement elm) {
        operations.add(new PatchOperation("replace", path, elm));
    }

    public void remove(String path) {
        operations.add(new PatchOperation("remove", path));
    }

    /*default protection*/ List<PatchOperation> getOperations() {
        return operations;
    }
    
    /*default protection*/ void addOperation(PatchOperation po) {
        operations.add(po);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
