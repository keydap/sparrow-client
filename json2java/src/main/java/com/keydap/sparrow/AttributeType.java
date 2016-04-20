/*
 * Copyright (c) 2016 Keydap Software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * See LICENSE file for complete details.
 */
package com.keydap.sparrow;


/**
 * 
 *
 * @author Kiran Ayyagari (kayyagari@keydap.com)
 */
public class AttributeType {

    private String name;
    private String javaType;
    private boolean multiValued = false;
    private boolean readOnly = false;

    public AttributeType( String name, String javaType )
    {
        if ( name == null || javaType == null )
        {
            throw new IllegalArgumentException( "Null value cannot be accepted" );
        }

        this.name = name;
        this.javaType = javaType;
    }

    public String getName() {
        return name;
    }

    public String getMethodName() {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    public String getJavaType() {
        return javaType;
    }

    public boolean isMultiValued() {
        return multiValued;
    }

    public void setMultiValued(boolean isMultiValued) {
        this.multiValued = isMultiValued;
    }

    public boolean isBoolean() {
        return "boolean".equalsIgnoreCase(javaType);
    }

    public int compareTo(Object o) {
        AttributeType that = (AttributeType) o;

        return getName().compareTo(that.getName());
    }

    @Override
    public boolean equals(Object obj) {
        return getName().equals(((AttributeType) obj).getName());
    }

    /**
     * @return the readOnly
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * @param readOnly
     *            the readOnly to set
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name + "=" + name;
    }

}
