/*
 * Copyright (c) 2018 Keydap Software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * See LICENSE file for details.
 */
package com.keydap.sparrow.rbac;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

/**
 *
 * @author Kiran Ayyagari (kayyagari@keydap.com)
 */
public class ResourcePermission implements Serializable {
    private String resName;
    private List<OperationPermission> opsArr;

    private static final Gson gson = new Gson();
    
    public ResourcePermission(String resName) {
        this.resName = resName;
        this.opsArr = new ArrayList<>();
    }

    public void add(OperationPermission op) {
        opsArr.add(op);
    }
    
    public String getResName() {
        return resName;
    }

    public List<OperationPermission> getOpsArr() {
        return opsArr;
    }
    
    public String toJson() {
        return gson.toJson(this);
    }

    public void setOpsArr(List<OperationPermission> opsArr) {
        this.opsArr = opsArr;
    }
    
    public OperationPermission getPermission(String opName) {
        if(opsArr != null) {
            for(OperationPermission op : opsArr) {
                if(op.getOp().equalsIgnoreCase(opName)) {
                    return op;
                }
            }
        }
        
        return null;
    }
}
