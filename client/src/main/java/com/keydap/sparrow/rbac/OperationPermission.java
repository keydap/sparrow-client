/*
 * Copyright (c) 2018 Keydap Software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * See LICENSE file for details.
 */
package com.keydap.sparrow.rbac;

import com.google.gson.Gson;

/**
 *
 * @author Kiran Ayyagari (kayyagari@keydap.com)
 */
public class OperationPermission {
    private String op;
    private String allowAttrs;
    private String denyAttrs;
    private String filter;

    private static final Gson gson = new Gson();

    private OperationPermission(String op, String filter) {
        this.op = op;
        this.filter = filter;
    }

    public static OperationPermission withAllowAttributes(String op,
            String allowAttrs, String filter) {
        OperationPermission p = new OperationPermission(op, filter);
        p.allowAttrs = allowAttrs;
        return p;
    }

    public static OperationPermission withDenyAttributes(String op,
            String denyAttrs, String filter) {
        OperationPermission p = new OperationPermission(op, filter);
        p.denyAttrs = denyAttrs;
        return p;
    }

    public String getOp() {
        return op;
    }

    public String getAllowAttrs() {
        return allowAttrs;
    }

    public String getDenyAttrs() {
        return denyAttrs;
    }

    public String getFilter() {
        return filter;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((op == null) ? 0 : op.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        OperationPermission other = (OperationPermission) obj;
        if (op == null) {
            if (other.op != null) {
                return false;
            }
        } else if (!op.equals(other.op)) {
            return false;
        }
        return true;
    }

    public String toJson() {
        return gson.toJson(this);
    }
    
    public String asJsonArray() {
        return "[" + gson.toJson(this) + "]";
    }
}
