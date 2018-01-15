/*
 * Copyright (c) 2018 Keydap Software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * See LICENSE file for details.
 */
package com.keydap.sparrow.rbac;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.Gson;

/**
 *
 * @author Kiran Ayyagari (kayyagari@keydap.com)
 */
public class ResourcePermission {
    private String resName;
    private Set<OperationPermission> opsArr;

    private static final Gson gson = new Gson();
    
    public ResourcePermission(String resName) {
        this.resName = resName;
        this.opsArr = new HashSet<>();
    }

    public void add(OperationPermission op) {
        opsArr.add(op);
    }
    
    public String getResName() {
        return resName;
    }

    public Set<OperationPermission> getOpsArr() {
        return opsArr;
    }
    
    public String toJson() {
        return gson.toJson(this);
    }
}
