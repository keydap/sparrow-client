/*
 * Copyright (c) 2016 Keydap Software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * See LICENSE file for details.
 */
package com.keydap.sparrow;

import java.util.List;

/**
 *
 * @author Kiran Ayyagari (kayyagari@keydap.com)
 */
public class SearchResponse<T> extends Response<T> {
    private int totalResults;
    private List<T> resources;
    private int startIndex;
    private int itemsPerPage;
    
    //TODO support stream reading
    //private InputStream stream;
    
    public int getTotalResults() {
        return totalResults;
    }

    public List<T> getResources() {
        return resources;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    void setResources(List<T> resources) {
        this.resources = resources;
    }

    void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public T getResource() {
        throw new UnsupportedOperationException("Call getResources() instead");
    }

    void setResource(T resource) {
        throw new UnsupportedOperationException("Call setResources(List<T>) instead");
    }
}
