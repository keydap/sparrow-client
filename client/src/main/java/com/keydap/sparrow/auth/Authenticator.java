/*
 * Copyright (c) 2016 Keydap Software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * See LICENSE file for details.
 */
package com.keydap.sparrow.auth;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 * The base interface for implementations of various authentication mechanisms.
 * 
 * @author Kiran Ayyagari (kayyagari@keydap.com)
 */
public interface Authenticator {
    /**
     * Authenticates with the service present at the given base URL
     * using the given HTTP client. Implementers can ignore the
     * given HTTP client and may use their own instance if required.
     * 
     * @param baseUrl the base URL of the SCIM service
     * @param client an instance of HttpClient
     * @throws whatever the exception encountered while performing the authentication
     */
    void authenticate(String baseUrl, CloseableHttpClient client) throws Exception;
    
    /**
     * Set any authorization headers on the request to be sent to the server. 
     * 
     * @param req a HTTP request
     */
    void addHeaders(HttpUriRequest req);
    
    /**
     * Preserve any renewed authentication header values for later user.
     * 
     * @param resp the HTTP response
     */
    void saveHeaders(HttpResponse resp);
}
