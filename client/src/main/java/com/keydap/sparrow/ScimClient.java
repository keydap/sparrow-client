/*
 * Copyright (c) 2016 Keydap Software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * See LICENSE file for details.
 */
package com.keydap.sparrow;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * A client for any SCIM v2.0 compliant server.
 * 
 * @author Kiran Ayyagari (kayyagari@keydap.com)
 */
public class ScimClient {

    /** the base API URL of the SCIM server e.g https://sparrow.keydap.com/v2 */
    private String baseApiUrl;

    // private String username;
    // private String password;

    private HttpClientBuilder builder;

    private CloseableHttpClient client;

    private Gson serializer;

    private static final Logger LOG = LoggerFactory.getLogger(ScimClient.class);
    
    private Map<String, Class<?>> endpointClassMap = new HashMap<String, Class<?>>();

    private Map<Class<?>, String> classEndpointMap = new HashMap<Class<?>, String>();

    private Map<String, Set<Field>> endpointExtFieldMap = new HashMap<String, Set<Field>>();

    private static final ContentType MIME_TYPE = ContentType
            .create("application/scim+json", HTTP.DEF_CONTENT_CHARSET);

    /**
     * Creates an instance of the client
     * 
     * @param baseApiUrl the API URL of the SCIM server
     */
    public ScimClient(String baseApiUrl) {
        this.baseApiUrl = baseApiUrl;
        builder = HttpClientBuilder.create().useSystemProperties();
        client = builder.build();

        GsonBuilder gb = new GsonBuilder();
        // gb.disableInnerClassSerialization();
        serializer = gb.create();
    }

    /**
     * Registers the given Resource classes.
     * 
     * @param resCls one or more Resource classes
     */
    public void register(Class<?>... resCls) {
        for (Class<?> rc : resCls) {
            Resource res = rc.getAnnotation(Resource.class);
            if(res == null) {
                LOG.warn("Resource annotation is missing, ignoring class {}", rc.getName());
                continue;
            }

            String schemaId = res.schemaId();
            if(schemaId.trim().length() == 0) {
                String err = "Invalid schemaId in Resource annotation of class " + rc.getName();
                LOG.warn(err);
                throw new IllegalArgumentException(err);
            }
            
            String endpoint = res.endpoint();
            if(endpoint.trim().length() == 0) {
                String err = "Invalid endpoint in Resource annotation of class " + rc.getName();
                LOG.warn(err);
                throw new IllegalArgumentException(err);
            }
            
            endpointClassMap.put(endpoint, rc);
            classEndpointMap.put(rc, endpoint);

            // the fields representing extended schema
            Set<Field> extResFields = new HashSet<Field>();

            Field[] fields = rc.getDeclaredFields();
            for (Field f : fields) {
                Extension ext = f.getAnnotation(Extension.class);
                if (ext != null) {
                    f.setAccessible(true);
                    extResFields.add(f);
                }
            }

            if (!extResFields.isEmpty()) {
                endpointExtFieldMap.put(endpoint, extResFields);
            }
        }
    }

    public <T> Response<T> addResource(T rs) {
        Class resClas = rs.getClass();
        String endpoint = classEndpointMap.get(resClas);
        HttpPost post = new HttpPost(baseApiUrl + endpoint);
        setBody(post, rs, endpoint, resClas);
        return sendRequest(post, resClas);
    }

    private <T> Response<T> sendRequest(HttpUriRequest req, Class<T> resClas) {
        try {
            LOG.debug("Sending {} request to {}", req.getMethod(), req.getURI());
            HttpResponse resp = client.execute(req);
            StatusLine sl = resp.getStatusLine();
            int code = sl.getStatusCode();
            
            LOG.debug("Received status code {} from the request to {}", code, req.getURI());
            HttpEntity entity = resp.getEntity();
            String json = null;
            if (entity != null) {
                json = EntityUtils.toString(entity);
            }
            
            Response<T> result = new Response<T>();
            result.setHttpCode(code);
            
            // if it is success there will be response body to read
            if (code == 200 || code == 201) {
                if(json != null) { // DELETE will have no response body, so check for null
                    T t = serializer.fromJson(json, resClas);
                    result.setResource(t);
                }
            } else {
                if(json != null) {
                    Error error = serializer.fromJson(json, Error.class);
                    result.setError(error);
                }
            }

            result.setHttpBody(json);
            
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> void setBody(
            HttpEntityEnclosingRequestBase req, T rs, String endpoint, Class<?> resClas) {
        JsonObject json = (JsonObject) serializer.toJsonTree(rs);
        
        JsonArray schemas = new JsonArray();
        schemas.add(resClas.getAnnotation(Resource.class).schemaId());
        
        Set<Field> extFields = endpointExtFieldMap.get(endpoint);
        if (extFields != null) {
            for (Field f : extFields) {
                JsonElement je = json.remove(f.getName());
                if(je != null) {
                    Extension extSchema = f.getAnnotation(Extension.class);
                    String schemaId = extSchema.value();
                    json.add(schemaId, je);
                    schemas.add(schemaId);
                }
            }
        }

        json.add("schemas", schemas);
        
        StringEntity entity = new StringEntity(json.toString(), MIME_TYPE);
        req.setEntity(entity);
    }

}
