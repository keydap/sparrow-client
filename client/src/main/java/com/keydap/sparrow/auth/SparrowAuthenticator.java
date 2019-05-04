/*
 * Copyright (c) 2016 Keydap Software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * See LICENSE file for details.
 */
package com.keydap.sparrow.auth;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * Authenticator for Sparrow server.
 * 
 * @author Kiran Ayyagari (kayyagari@keydap.com)
 */
public class SparrowAuthenticator implements Authenticator {
    
    /** the JWT received after authentication */
    private String token = null;
    
    /** the authentication parameters required by the Sparrow server */
    private AuthRequest authReq;

    private static final Logger LOG = LoggerFactory.getLogger(SparrowAuthenticator.class);
    
    /**
     * Creates an authenticator instance
     * 
     * @param username the user identifier
     * @param domain name of the domain to which user belongs
     * @param password password of the user
     */
    public SparrowAuthenticator(String username, String domain, String password) {
        authReq = new AuthRequest(username, domain, password);
    }
    
    @SuppressWarnings("unused")
    private class AuthRequest {
        private String username;
        private String domain;
        private String password;

        AuthRequest(String username, String domain, String password) {
            this.username = username;
            this.domain = domain;
            this.password = password;
        }
    }
    
    @Override
    public void authenticate(String baseUrl, CloseableHttpClient client) throws Exception {
        HttpPost post = new HttpPost(baseUrl + "/directLogin");
        String body = new Gson().toJson(authReq);
        StringEntity entity = new StringEntity(body, ContentType.APPLICATION_JSON);
        post.setEntity(entity);
        
        HttpResponse resp = client.execute(post);
        StatusLine sl = resp.getStatusLine();
        
        if((sl.getStatusCode() != 200) && (sl.getStatusCode() != 201)) {
            String msg = EntityUtils.toString(resp.getEntity());
            LOG.warn(msg);
            throw new IllegalStateException(msg);
        }
        
        LOG.debug("Successfully authenticated");
        token = EntityUtils.toString(resp.getEntity());
    }

    @Override
    public void addHeaders(HttpUriRequest req) {
        req.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }

    @Override
    public void saveHeaders(HttpResponse resp) {
    }

    /**
     * The JWToken received from the server after authentication.
     * 
     * @return the JWToken
     */
    public String getToken() {
        return token;
    }
}
