/*
 * Copyright (c) 2016 Keydap Software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * See LICENSE file for complete details.
 */
package com.keydap.sparrow;

import java.util.List;

/**
 * 
 *
 * @author Kiran Ayyagari (kayyagari@keydap.com)
 */
public class AttributeType {

    String name;
    String type;
    boolean multiValued;
    String description;
    String mutability;
    
    String schema;
    boolean extension;
    //boolean required;
    //boolean caseExact;
    //String returned;
    //String uniqueness;

    List<AttributeType> subAttributes;

    /**
     * @return the readOnly
     */
    public boolean isReadOnly() {
        return "readOnly".equalsIgnoreCase(mutability);
    }

    public boolean isBoolean() {
        return "boolean".equalsIgnoreCase(type);
    }
    
    public String getMethodName()
    {
        return Character.toUpperCase( name.charAt( 0 ) ) + name.substring( 1 );
    }

    @Override
    public String toString() {
        return name + "=" + name;
    }

    // for StringTemplate's sake adding GETXXX
    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getSchema() {
        return schema;
    }

    public boolean isExtension() {
        return extension;
    }
}
