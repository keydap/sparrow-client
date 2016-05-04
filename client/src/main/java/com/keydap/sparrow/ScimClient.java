/*
 * Copyright (c) 2016 Keydap Software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * See LICENSE file for details.
 */
package com.keydap.sparrow;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * A client for any SCIM v2.0 compliant server.
 * 
 * @author Kiran Ayyagari (kayyagari@keydap.com)
 */
//@SuppressWarnings("all")
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

    public static final ContentType MIME_TYPE = ContentType
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
        
        Type dt = new TypeToken<Date>(){}.getType();
        gb.registerTypeAdapter(dt, new DateTimeSerializer());
        
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
        String endpoint = getEndpoint(resClas);
        HttpPost post = new HttpPost(baseApiUrl + endpoint);
        setBody(post, rs);
        return sendRawRequest(post, resClas);
    }

    public <T> Response<T> replaceResource(T rs) {
        Class resClas = rs.getClass();
        String endpoint = getEndpoint(resClas);
        HttpPut put = new HttpPut(baseApiUrl + endpoint);
        setBody(put, rs);
        return sendRawRequest(put, resClas);
    }

    public <T> Response<T> modifyResource(T rs) {
        Class resClas = rs.getClass();
        String endpoint = getEndpoint(resClas);
        HttpPatch patch = new HttpPatch(baseApiUrl + endpoint);
        setBody(patch, rs);
        return sendRawRequest(patch, resClas);
    }

    public Response<Boolean> deleteResource(String id, Class resourceType) {
        String endpoint = getEndpoint(resourceType);
        HttpDelete delete = new HttpDelete(baseApiUrl + endpoint + "/" + id);
        Response<Boolean> resp = sendRawRequest(delete, resourceType);
        if(resp.getHttpCode() == 200) {
            resp.setResource(true);
        }else {
            resp.setResource(false);
        }
        
        return resp;
    }
    
    public <T> Response<T> getResource(String id, Class<T> resClas) {
        String endpoint = getEndpoint(resClas);
        HttpGet get = new HttpGet(baseApiUrl + endpoint + "/" + id);
        return sendRawRequest(get, resClas);
    }

    public <T> SearchResponse<T> searchResource(Class<T> resClas) {
        String endpoint = getEndpoint(resClas);
        
        StringBuilder url = new StringBuilder(baseApiUrl);
        url.append(endpoint);
        
        HttpGet get = new HttpGet(url.toString());
        return sendSearchRequest(get, resClas);
    }
    
    public <T> SearchResponse<T> searchResource(String filter, Class<T> resClas) {
        String endpoint = getEndpoint(resClas);
        
        StringBuilder url = new StringBuilder(baseApiUrl);
        url.append(endpoint);
        
        if(filter != null) {
            url.append("/?filter=");
            try {
                url.append(URLEncoder.encode(filter, "utf-8"));
            }
            catch(Exception e) {
                throw new RuntimeException(e);
            }
        }

        HttpGet get = new HttpGet(url.toString());
        return sendSearchRequest(get, resClas);   
    }
    
    public <T> SearchResponse<T> searchResource(String filter, Class<T> resClas, String... attributes) {
        return searchResource(filter, resClas, attributes, true);
    }
    
    public <T> SearchResponse<T> searchResource(SearchRequest sr, Class<T> resClas) {
        String endpoint = getEndpoint(resClas);
        
        StringBuilder url = new StringBuilder(baseApiUrl);
        url.append(endpoint).append("/.search");
        
        HttpPost post = new HttpPost(url.toString());
        String json = serializer.toJson(sr);
        StringEntity entity = new StringEntity(json, MIME_TYPE);
        post.setEntity(entity);

        return sendSearchRequest(post, resClas);        
    }
    
    public <T> SearchResponse<T> searchResource(String filter, Class<T> resClas, String[] attributes, boolean include) {
        SearchRequest sr = new SearchRequest();
        sr.setFilter(filter);
        
        if (attributes != null) {
            int i = 0;
            StringBuilder sb = new StringBuilder();
            for(; i < attributes.length-1; i++) {
                sb.append(attributes[i]).append(',');
            }
            sb.append(attributes[i]);
            
            if(include) {
                sr.setAttributes(sb.toString());
            } else {
                sr.setExcludedAttributes(sb.toString());
            }
        }
        
        return searchResource(sr, resClas);
    }

    public <T> SearchResponse<T> sendSearchRequest(HttpUriRequest req, Class<T> resClas) {
        if (!((req instanceof HttpGet) || (req instanceof HttpPost))) {
            throw new IllegalArgumentException("Invalid HTTP method " + req.getMethod() + ", only GET and POST are allowed for searching");
        }
        
        SearchResponse<T> result = new SearchResponse<T>();
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

            result.setHttpBody(json);
            result.setHttpCode(code);
            result.setHeaders(resp.getAllHeaders());
            
            // if it is success there will be response body to read
            if (code == 200) {
                if(json != null) { // DELETE will have no response body, so check for null
                    
                    JsonObject obj = (JsonObject) new JsonParser().parse(json);
                    JsonElement je = obj.get("totalResults");
                    if(je != null) {
                        result.setTotalResults(je.getAsInt());
                    }
                    
                    je = obj.get("startIndex");
                    if(je != null) {
                        result.setStartIndex(je.getAsInt());
                    }

                    je = obj.get("itemsPerPage");
                    if(je != null) {
                        result.setItemsPerPage(je.getAsInt());
                    }
                    
                    je = obj.get("Resources"); // yes, the 'R' in resources must be upper case
                    if(je != null) {
                        JsonArray arr = je.getAsJsonArray();
                        Iterator<JsonElement> itr = arr.iterator();
                        List<T> resources = new ArrayList<T>();
                        while(itr.hasNext()) {
                            JsonObject r = (JsonObject) itr.next();
                            resources.add(serializer.fromJson(r, resClas));
                        }
                        
                        if(!resources.isEmpty()) {
                            result.setResources(resources);
                        }
                    }
                }
            } else {
                if(json != null) {
                    Error error = serializer.fromJson(json, Error.class);
                    result.setError(error);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOG.warn("", e);
            result.setHttpCode(-1);
            Error err = new Error();
            
            err.setDetail(e.getMessage());
            result.setError(err);
        }
        return result;
    }

    public <T> Response<T> sendRawRequest(HttpUriRequest req, Class<T> resClas) {
        Response<T> result = new Response<T>();
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
            result.setHeaders(resp.getAllHeaders());
        } catch (Exception e) {
            LOG.warn("", e);
            result.setHttpCode(-1);
            Error err = new Error();
            
            err.setDetail(e.getMessage());
            result.setError(err);
        }
        
        return result;
    }

    private <T> void setBody(HttpEntityEnclosingRequestBase req, T rs) {
        String data = serialize(rs).toString();
        StringEntity entity = new StringEntity(data, MIME_TYPE);
        req.setEntity(entity);
    }

    public <T> JsonObject serialize(T rs) {
        JsonObject json = (JsonObject) serializer.toJsonTree(rs);
        
        JsonArray schemas = new JsonArray();
        Resource r = rs.getClass().getAnnotation(Resource.class);
        schemas.add(r.schemaId());
        
        Set<Field> extFields = endpointExtFieldMap.get(r.endpoint());
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
        
        return json;
    }
    
    private String getEndpoint(Class resClas) {
        String ep = classEndpointMap.get(resClas);
        if (ep == null) {
            throw new IllegalArgumentException("There is no endpoint found with the given resource class. Resource class must be registered to avoid this error");
        }
        
        return ep;
    }
}
