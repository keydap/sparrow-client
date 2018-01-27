/*
 * Copyright (c) 2017 Keydap Software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * See LICENSE file for details.
 */
package com.keydap.sparrow;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.keydap.sparrow.PatchRequest.PatchOperation;

/**
 *
 * @author Kiran Ayyagari (kayyagari@keydap.com)
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class PatchGenerator {
    private static final String OPERATOR_AND = " AND ";

    private static final String OPERATOR_EQ = " EQ ";

    private static final Gson serializer = new Gson();
    
    private static final Map<Class, List<Field>> fieldMap = new HashMap<>();
    
    private List<Field> getFields(Class cls) {
        List<Field> fields = fieldMap.get(cls);
        if(fields == null) {
            fields = new ArrayList<>();
            Field[] tmp = cls.getDeclaredFields();
            for(Field f : tmp) {
                // skip static fields
                if(Modifier.isStatic(f.getModifiers())) {
                    continue;
                }

                // skip readonly fields
                if(f.isAnnotationPresent(ReadOnly.class)) {
                    continue;
                }
                
                f.setAccessible(true);
                fields.add(f);
            }
            
            fieldMap.put(cls, fields);
        }
        
        return fields;
    }
    
    public PatchRequest create(String id, Object modified, Object original) {
        return create(id, modified, original, null);
    }
    
    public PatchRequest create(String id, Object modified, Object original, String etag) {
        if(modified.getClass() != original.getClass()) {
            throw new IllegalArgumentException("PatchRequest cannot be created for two different types ");
        }

        try {
            PatchRequest pr = new PatchRequest(id, modified.getClass(), etag);
            _generate(pr, modified, original);
            return pr;
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void updatePr(String op, PatchRequest pr, String pathStr, Object value) {

        JsonElement jeVal = null;
        if(value instanceof JsonElement) {
            jeVal = (JsonElement) value;
        }
        
        switch (op) {
        case "add":
            if(jeVal == null) {
                jeVal = serializer.toJsonTree(value);
            }
            pr.add(pathStr, jeVal);
            break;

        case "remove":
            pr.remove(pathStr);
            break;
            
        case "replace":
            if(jeVal == null) {
                jeVal = serializer.toJsonTree(value);
            }
            pr.replace(pathStr, jeVal);
            break;
            
        default:
            throw new IllegalArgumentException("unknown operation " + op);
        }
    }
    
    private void _generate(PatchRequest pr, Object modified, Object original) throws Exception {
        Class cls = modified.getClass();
        for(Field f : getFields(cls)) {
            Object m = f.get(modified);
            Object o = f.get(original);
            
            String path = f.getName();
            Class fType = f.getType();
            
            Extension ext = f.getAnnotation(Extension.class);
            if(ext != null) {
                path = ext.value();
            }
            
            //System.out.println("path -> " + path);
            if(m == o) {
                continue;
            }
            else if((m != null) && (o == null)) {
                updatePr("add", pr, path, m);
            }
            else if((m == null) && (o != null)) {
                updatePr("remove", pr, path, null);
            }
            else if(fType.isAssignableFrom(List.class)) {
                    diffCollections(pr, path, (List)m, (List)o);
            }
            else if(ext != null) {
                PatchRequest p2 = new PatchRequest(pr.getId(), fType);
                _generate(p2, m, o);
                for(PatchOperation po : p2.getOperations()) {
                    po.setPath(path  + ":" + po.getPath());
                    pr.addOperation(po);
                }
            }
            else {
                diffNonNullObjects(pr, path, m, o);
            }
        }
    }
    
    private void diffNonNullObjects(PatchRequest pr, String path, Object modified, Object original) throws Exception {
        Class cls = modified.getClass();
        boolean pt = isPrimitive(cls);
        if(pt) {
            if(modified.equals(original)) {
                return;
            }
            
            updatePr("replace", pr, path, modified);
            return;
        }
        
        // construct path like ims[type=\"home\"]
        path = buildPathWithFilter(path, original);
        
        JsonObject obj = new JsonObject();
        for(Field f : getFields(cls)) {
            Object m = f.get(modified);
            Object o = f.get(original);
            
            String name = f.getName();
            
            if(m == o) {
                continue;
            }
            else if((m != null) && (o == null)) {
                obj.add(name, serializer.toJsonTree(m));
            }
            else if((m == null) && (o != null)) {
                // do nothing
            }
            else if(!m.equals(o)) {
                obj.add(name, serializer.toJsonTree(m));
            }
        }
        
        if(!obj.keySet().isEmpty()) {
            updatePr("replace", pr, path, obj);
        }
    }
    
    private boolean isPrimitive(Class c) {
        if(c.isPrimitive()) {
            return true;
        }
        
        Class[] primitiveTypes = {String.class, Integer.class, Double.class, Boolean.class, Number.class, Float.class, Byte.class};
        for(Class p : primitiveTypes) {
            if(c == p) {
                return true;
            }
        }
        
        return false;
    }
    
    /*default protected*/ String buildPathWithFilter(String fieldName, Object original) throws Exception {
        // for non-multivalued complextype(e.g Name) return the fieldName 
        ComplexType ct = original.getClass().getAnnotation(ComplexType.class);
        if(ct != null && !ct.multival()) {
            return fieldName;
        }
        
        StringBuilder pathBuilder = new StringBuilder(fieldName).append("[");
        for(Field f : getFields(original.getClass())) {
            String name = f.getName();
            Class fType = f.getType();

            Object o = f.get(original);
            if(o == null) {
                continue;
            }
            
            pathBuilder.append(name).append(OPERATOR_EQ);
            if(fType == String.class) {
                String val = o.toString().replaceAll("\"", "\\\\\"");
                pathBuilder.append("\"").append(val).append("\"");
            }
            else {
                pathBuilder.append(o);
            }
            
            pathBuilder.append(OPERATOR_AND);
        }
        
        int pos = pathBuilder.lastIndexOf(OPERATOR_AND);
        pathBuilder.delete(pos, pathBuilder.length());
        pathBuilder.append("]");

        return pathBuilder.toString();
    }
    
    private void diffCollections(PatchRequest pr, String path, List m, List o) throws Exception {
        int mSize = m.size();
        int oSize = o.size();
        
        //System.out.println("list path -> " + path);
        // can happen in case if the modified list is empty
        if(mSize == 0 && oSize > 0) {
            updatePr("remove", pr, path, null);
            return;
        }

        JsonArray arr = new JsonArray();
        for(int i=0; i< mSize; i++) {
            Object mObj = m.get(i);
            if(i< oSize) {
                Object oObj = o.get(i);
                // deal with deletion
                if(mObj == null && oObj != null) {
                    updatePr("remove", pr, buildPathWithFilter(path, oObj), null);
                }
                else {
                    diffNonNullObjects(pr, path, mObj, oObj);
                }
            }
            else {
                if(mObj != null) {
                    arr.add(serializer.toJsonTree(mObj));
                }
            }
        }
        
        if(arr.size() > 0) {
            updatePr("add", pr, path, arr);
        }
        
        /*
        if(mSize < oSize) {
            for(int i=0; i< oSize; i++) {
                Object oObj = o.get(i);
                if(i < mSize) {
                    Object mObj = m.get(i);
                    diffObjects(pr, path, mObj, oObj);
                }
                else {
                    String atPath = buildPath(path, oObj);
                    updatePr("remove", pr, atPath, null);
                }
            }
        }
        else {
            for(int i=0; i< oSize; i++) {
                Object oObj = o.get(i);
                Object mObj = m.get(i);
                diffObjects(pr, path, mObj, oObj);
            }
        }*/
    }
}
