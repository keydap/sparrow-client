/*
 * Copyright (c) 2016 Keydap Software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * See LICENSE file for details.
 */
package com.keydap.sparrow;

import java.util.Arrays;

/**
 * SCIM v2 search request
 * 
 * @author Kiran Ayyagari (kayyagari@keydap.com)
 */
public class SearchRequest {
    private String[] schemas = {"urn:ietf:params:scim:api:messages:2.0:SearchRequest"};
    private String attributes;
    private String excludedAttributes;
    private String filter;
    private String sortBy;
    private String sortOrder;
    private int startIndex;
    private int count;

    public String[] getSchemas() {
        return schemas;
    }

    public void setSchemas(String[] schemas) {
        this.schemas = schemas;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public String getExcludedAttributes() {
        return excludedAttributes;
    }

    public void setExcludedAttributes(String excludedAttributes) {
        this.excludedAttributes = excludedAttributes;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "SearchRequest [schemas=" + Arrays.toString(schemas)
                + ", attributes=" + attributes + ", excludedAttributes="
                + excludedAttributes + ", filter=" + filter + ", sortBy="
                + sortBy + ", sortOrder=" + sortOrder + ", startIndex="
                + startIndex + ", count=" + count + "]";
    }
}
