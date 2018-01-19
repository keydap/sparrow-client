/*
 * Copyright (c) 2016 Keydap Software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * See LICENSE file for details.
 */
package com.keydap.sparrow;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_MODIFIED;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;

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

import javax.net.ssl.SSLContext;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
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
import com.keydap.sparrow.auth.Authenticator;

/**
 * A client for any SCIM v2.0 compliant server.
 * 
 * @author Kiran Ayyagari (kayyagari@keydap.com)
 */
@SuppressWarnings("all")
public class SparrowClient {

    /** the base API URL of the SCIM server e.g https://sparrow.keydap.com/v2 */
    private String baseApiUrl;

    /** the base API URL of the OAuth server e.g https://sparrow.keydap.com/oauth2 */
    private String baseOauthUrl;
    
    /** the authenticator instance */
    private Authenticator authenticator;
    
    /** HTTP client builder */
    private HttpClientBuilder builder;

    /** the main HTTP client instance used for communicating with the server */
    private CloseableHttpClient client;

    /** SCIM entity serializer and deserializer */
    private Gson serializer;

    /** the logger instance */
    private static final Logger LOG = LoggerFactory.getLogger(SparrowClient.class);
    
    /** map holding <endpoint-entityClass> tuples */
    private Map<String, Class<?>> endpointClassMap = new HashMap<String, Class<?>>();

    /** map holding <schemaId-entityClass> tuples */
    private Map<String, Class<?>> schemaIdClassMap = new HashMap<String, Class<?>>();

    /** map holding <entityClass-endpoint> tuples */
    private Map<Class<?>, String> classEndpointMap = new HashMap<Class<?>, String>();

    private Map<String, Set<Field>> endpointExtFieldMap = new HashMap<String, Set<Field>>();

    /** the MIME type for application/scim+json content */
    public static final ContentType MIME_TYPE = ContentType
            .create("application/scim+json", Consts.UTF_8);

    /** type for serializing a List of JsonObjects */
    private static final Type lstJsonObj = new TypeToken<List<JsonObject>>(){}.getType();
    
    private JsonParser parser = new JsonParser();
    
    /**
     * Creates an instance of the client
     * 
     * @param baseApiUrl the API URL of the SCIM server
     */
    public SparrowClient(String baseApiUrl) {
        this(baseApiUrl, (String)null);
    }

    /**
     * Creates an instance of the client
     * 
     * @param baseApiUrl the API URL of the SCIM server
     * @param baseOauthUrl the API URL of the Oauth server
     */
    public SparrowClient(String baseApiUrl, String baseOauthUrl) {
        this(baseApiUrl, baseOauthUrl, null);
        
    }

    /**
     * Creates an instance of the client
     * 
     * @param baseApiUrl the API URL of the SCIM server
     * @param authenticator authenticator instance, optional
     */
    public SparrowClient(String baseApiUrl, Authenticator authenticator) {
        this(baseApiUrl, null, authenticator);
    }

    /**
     * Creates an instance of the client
     * 
     * @param baseApiUrl the API URL of the SCIM server
     * @param baseOauthUrl the API URL of the Oauth server
     * @param authenticator authenticator instance, optional
     */
    public SparrowClient(String baseApiUrl, String baseOauthUrl, Authenticator authenticator) {
        this(baseApiUrl, baseOauthUrl, authenticator, null);
    }

    /**
     * Creates an instance of the client
     * 
     * @param baseApiUrl the API URL of the SCIM server
     * @param authenticator authenticator instance, optional
     * @param sslCtx the SSL context, mandatory only when the service is accessible over HTTPS
     */
    public SparrowClient(String baseApiUrl, String baseOauthUrl, Authenticator authenticator, SSLContext sslCtx) {
        this.baseApiUrl = baseApiUrl;
        this.baseOauthUrl = baseOauthUrl;
        
        // if authenticator is not given then use a null authenticator
        if(authenticator == null) {
            authenticator = new Authenticator() {
                public void saveHeaders(HttpResponse resp) {
                }
                
                public void authenticate(String baseUrl, CloseableHttpClient client)
                        throws Exception {
                }
                
                public void addHeaders(HttpUriRequest req) {
                }
            };
        }
        
        this.authenticator = authenticator;
        
        boolean isHttps = baseApiUrl.toLowerCase().startsWith("https");
        
        builder = HttpClientBuilder.create().useSystemProperties();
        
        if(isHttps) {
            builder.setSSLContext(sslCtx);
        }
        
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
            
            schemaIdClassMap.put(schemaId, rc);
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
    
    /**
     * Performs authentication using the authenticator
     * 
     * @throws Exception when the authenticator throws any exception
     */
    public void authenticate() throws Exception {
        authenticator.authenticate(baseApiUrl, client);
    }
    
    /**
     * Adds the given resource
     * 
     * @param rs the resource
     * @return
     */
    public <T> Response<T> addResource(T rs) {
        Class resClas = rs.getClass();
        String endpoint = getEndpoint(resClas);
        HttpPost post = new HttpPost(baseApiUrl + endpoint);
        setBody(post, rs);
        return sendRawRequest(post, resClas);
    }

    /**
     * Replaces the given resource
     * 
     * @param id identifier of the resource to be replaced
     * @param rs the new resource with which old one will be replaced
     * @return
     */
    public <T> Response<T> replaceResource(String id, T rs) {
        return replaceResource(id, rs, null);
    }
    
    /**
     * Replaces the given resource 
     * 
     * @param id identifier of the resource to be replaced
     * @param rs the new resource with which old one will be replaced
     * @param ifNoneMatch the value to be set for If-None-Match header
     * @return
     */
    public <T> Response<T> replaceResource(String id, T rs, String ifNoneMatch) {
        Class resClas = rs.getClass();
        String endpoint = getEndpoint(resClas);
        HttpPut put = new HttpPut(baseApiUrl + endpoint + "/" + id);
        setIfNoneMatch(put, ifNoneMatch);
        setBody(put, rs);
        return sendRawRequest(put, resClas);
    }

    /**
     * Modifies the selected resource
     * @param pr the modify(a.k.a patch) request
     * @return
     */
    public <T> Response<T> patchResource(PatchRequest pr) {
        Class resClas = pr.getResClass();
        String endpoint = getEndpoint(resClas);
        
        String url = baseApiUrl + endpoint + "/" + pr.getId();
        
        if(pr.getAttributes() != null) {
            String encoded;
            try {
                encoded = URLEncoder.encode(pr.getAttributes(), Consts.UTF_8.name());
            }
            catch(Exception e) {
                throw new RuntimeException(e);
            }
            
            url += "?attributes=" + encoded;
        }
        
        HttpPatch patch = new HttpPatch(url);
        setIfNoneMatch(patch, pr.getIfNoneMatch());
        setBody(patch, pr);
        return sendRawRequest(patch, resClas);
    }

    /**
     * Deletes the selected resource
     * 
     * @param id identifier of the resource to be deleted
     * @param resourceType the type of the resource that is to be deleted
     * @return
     */
    public Response<Boolean> deleteResource(String id, Class resourceType) {
        String endpoint = getEndpoint(resourceType);
        HttpDelete delete = new HttpDelete(baseApiUrl + endpoint + "/" + id);
        Response<Boolean> resp = sendRawRequest(delete, resourceType);
        if(resp.getHttpCode() == SC_NO_CONTENT) {
            resp.setResource(true);
        }else {
            resp.setResource(false);
        }
        
        return resp;
    }
    
    /**
     * Fetches the resource specified by the given identifier
     * 
     * @param id identifier of the resource
     * @param resClas the type of the resource to be fetched
     * @return
     */
    public <T> Response<T> getResource(String id, Class<T> resClas) {
        return getResource(id, null, resClas, true, "*");
    }
    
    /**
     * Fetches the resource specified by the given identifier.
     * If the version of the resource matched with the value of 
     * given value of ifNoneMatch parameter then the status result
     * 304 (NOT_MODIFIED) will be returned withoud any body
     * 
     * @param id identifier of the resource
     * @param ifNoneMatch the value of the resource's version
     * @param resClas the type of the resource to be fetched
     * @return
     */
    public <T> Response<T> getResource(String id, String ifNoneMatch, Class<T> resClas) {
        return getResource(id, ifNoneMatch, resClas, false, null);
    }
    
    /**
     * Same as {@link #getResource(String, String, Class, boolean, String...)} 
     * but without the If-None-Match header value
     * 
     * @see #getResource(String, String, Class, boolean, String...)
     */
    public <T> Response<T> getResource(String id, Class<T> resClas, boolean include, String... attributes) {
        return getResource(id, null, resClas, include, attributes);
    }
    
    /**
     * Fetches the resource specified by the given identifier.
     * If the version of the resource matched with the value of 
     * given value of ifNoneMatch parameter then the status result
     * 304 (NOT_MODIFIED) will be returned withoud any body.
     * 
     * The requested resource's attributes will be filtered based on the
     * specified attributes and the include flag.
     * 
     * @param id identifier of the resource
     * @param ifNoneMatch the value of the resource's version
     * @param resClas the type of the resource to be fetched
     * @param include the flag to determine if the attributes specified 
     *                should be included or excluded from the resource
     * @param attributes the attribute names
     * @return
     */
    public <T> Response<T> getResource(String id, String ifNoneMatch, Class<T> resClas, boolean include, String... attributes) {
        String endpoint = getEndpoint(resClas);
        StringBuilder sb = new StringBuilder(baseApiUrl + endpoint + "/" + id);
        if(attributes != null) {
            if(include) {
                sb.append("?attributes=");
            } else {
                sb.append("?excludedAttributes=");
            }
            
            int i=0;
            for(; i < attributes.length - 1; i++) {
                String attr = attributes[i].trim();
                if(attr.length() > 0 ) {
                    sb.append(attr).append(',');
                }
            }
            String attr = attributes[i].trim();
            if(attr.length() > 0 ) {
                sb.append(attr);
            }
        }
        
        HttpGet get = new HttpGet(sb.toString());
        setIfNoneMatch(get, ifNoneMatch);
        return sendRawRequest(get, resClas);
    }

    /**
     * Fetches all the resources of the specified type
     * 
     * @param resClas the type of the resources to be fetched
     * @return
     */
    public <T> SearchResponse<T> searchResource(Class<T> resClas) {
        String endpoint = getEndpoint(resClas);
        
        StringBuilder url = new StringBuilder(baseApiUrl);
        url.append(endpoint);
        
        HttpGet get = new HttpGet(url.toString());
        return sendSearchRequest(get, resClas);
    }
    
    /**
     * Fetches all the resources of the specified type matching the given search filter
     * 
     * @param filter the search filer
     * @param resClas the type of the resources to be searched
     * @return
     */
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
    
    /**
     * Fetches all the resources of the specified type matching the given search filter.
     * The resources will contain only the given set of attributes besides the mandatory
     * attributes, remaining attributes will not be received.
     * 
     * @param filter the search filer
     * @param resClas the type of the resources to be searched
     * @param attributes the attributes that the fetched resources should contain (besides the mandatory attributes)
     * @return
     */
    public <T> SearchResponse<T> searchResource(String filter, Class<T> resClas, String... attributes) {
        return searchResource(filter, resClas, true, attributes);
    }
    
    /**
     * Fetches all resources of the given resourcetype based on the given search request.
     * 
     * @param sr the search request
     * @param resClas the type of the resources to be searched
     * @return
     */
    public <T> SearchResponse<T> searchResource(SearchRequest sr, Class<T> resClas) {
        String endpoint = getEndpoint(resClas);
        return _searchResource(sr, endpoint, resClas);
    }
    
    /**
     * Fetches all resources based on the criteria present in the given search request
     * 
     * @param sr the search request
     * @return
     */
    public SearchResponse<Object> searchAll(SearchRequest sr) {
        return _searchResource(sr, "", null);
    }
    
    /**
     * Fetches all the resources of the specified type matching the given search filter.
     * Based on the include flag value the given attributes may be included or excluded.
     * The mandatory attributes will always be returned irrespective of the value of the
     * include flag's value.
     * 
     * @param filter the search filer
     * @param resClas the type of the resources to be searched
     * @param include flag that determines inclusion or exclusion of the given attributes
     * @param attributes the attributes that must be included or excluded
     * @return
     */
    public <T> SearchResponse<T> searchResource(String filter, Class<T> resClas, boolean include, String... attributes) {
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

    /**
     * Fetches the service provider's configuration.
     * 
     * @return
     */
    public Response<JsonObject> getSrvProvConf() {
        HttpGet get = new HttpGet(baseApiUrl + "/ServiceProviderConfig");
        return sendRawRequest(get, JsonObject.class);
    }

    /**
     * Fetches all resourcetypes supported by the service provider.
     * 
     * @return
     */
    public Response<List<JsonObject>> getResTypes() {
        HttpGet get = new HttpGet(baseApiUrl + "/ResourceTypes");
        Response resp = sendRawRequest(get, JsonElement.class);
        JsonElement je = (JsonElement) resp.getResource();
        if(je != null) {
            List<JsonObject> lst = serializer.fromJson(je, lstJsonObj);
            resp.setResource(lst);
        }
        
        return resp;
    }

    /**
     * Fetches the given resourcetype's definition
     * 
     * @param name name of the resourcetype to be fetched
     * @return
     */
    public Response<JsonObject> getResType(String name) {
        HttpGet get = new HttpGet(baseApiUrl + "/ResourceTypes/" + name);
        return sendRawRequest(get, JsonObject.class);
    }

    /**
     * Fetches all schemas supported by the service provider.
     * 
     * @return
     */
    public Response<List<JsonObject>> getSchemas() {
        HttpGet get = new HttpGet(baseApiUrl + "/Schemas");
        Response resp = sendRawRequest(get, JsonElement.class);
        JsonElement je = (JsonElement) resp.getResource();
        if(je != null) {
            List<JsonObject> lst = serializer.fromJson(je, lstJsonObj);
            resp.setResource(lst);
        }
        
        return resp;
    }

    /**
     * Fetches the schema with the given identifier
     * 
     * @param id identifier of the schema to be fetched
     * @return
     */
    public Response<JsonObject> getSchema(String id) {
        HttpGet get = new HttpGet(baseApiUrl + "/Schemas/" + id);
        return sendRawRequest(get, JsonObject.class);
    }

    private <T> SearchResponse<T> _searchResource(SearchRequest sr, String endpoint, Class<T> resClas) {
        StringBuilder url = new StringBuilder(baseApiUrl);
        url.append(endpoint).append("/.search");
        
        HttpPost post = new HttpPost(url.toString());
        String json = serializer.toJson(sr);
        StringEntity entity = new StringEntity(json, MIME_TYPE);
        post.setEntity(entity);

        return sendSearchRequest(post, resClas);        
    }
    
    private <T> SearchResponse<T> sendSearchRequest(HttpUriRequest req, Class<T> resClas) {
        SearchResponse<T> result = new SearchResponse<T>();
        try {
            LOG.debug("Sending {} request to {}", req.getMethod(), req.getURI());
            authenticator.addHeaders(req);
            HttpResponse resp = client.execute(req);
            authenticator.saveHeaders(resp);
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
                            if (resClas != null) {
                                resources.add(unmarshal(r, resClas));
                            } else {
                                T rsObj = unmarshal(r);
                                if(rsObj == null) {
                                    LOG.warn("No resgistered resource class found to deserialize the resource data {}", r);
                                } else {
                                    resources.add(rsObj);
                                }
                            }
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

    private <T> T unmarshal(JsonObject json) throws Exception {
        JsonArray schemas = json.get("schemas").getAsJsonArray();
        Iterator<JsonElement> itr = schemas.iterator();
        
        T obj = null;
        
        while(itr.hasNext()) {
            String id = itr.next().getAsString();
            Class<?> rc = schemaIdClassMap.get(id);
            if(rc != null) {
                obj = (T) unmarshal(json, rc);
                break;
            }
        }
        
        return obj;
    }
    
    /**
     * Sends the given request to the server
     * 
     * @param req the HTTP request
     * @param resClas class of the resourcetype
     * @return
     */
    public <T> Response<T> sendRawRequest(HttpUriRequest req, Class<T> resClas) {
        Response<T> result = new Response<T>();
        try {
            authenticator.addHeaders(req);
            LOG.debug("Sending {} request to {}", req.getMethod(), req.getURI());
            HttpResponse resp = client.execute(req);
            authenticator.saveHeaders(resp);
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
            if (code == SC_OK || code == SC_CREATED || code == SC_NOT_MODIFIED) {
                if(json != null) { // some responses have no body, so check for null
                    T t = unmarshal(json, resClas);
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

    /**
     * Serializes the given resourcetype instance
     * 
     * @param rs resourcetype's instance
     * @return
     */
    public <T> JsonObject serialize(T rs) {
        JsonObject json = (JsonObject) serializer.toJsonTree(rs);
        
        Resource r = rs.getClass().getAnnotation(Resource.class);
        if(r != null) {
            JsonArray schemas = new JsonArray();
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
        }
        
        return json;
    }
    
    /**
     * Registers an application with the details present in the given request
     * 
     * @param appReq application request
     * @return a response containing RegisteredApp if successful or an Error when not
     */
    public Response<RegisteredApp> registerApp(RegisterAppRequest appReq) {
        String template = baseOauthUrl + "/register";
        
        HttpPost register = new HttpPost(template);
        String json = serializer.toJson(appReq);
        StringEntity entity = new StringEntity(json, MIME_TYPE);
        register.setEntity(entity);
        authenticator.addHeaders(register);
        
        Response<RegisteredApp> result = new Response<RegisteredApp>();
        try {
            HttpResponse regResp = client.execute(register);
            StatusLine sl = regResp.getStatusLine();
            
            result.setHttpCode(sl.getStatusCode());
            json = EntityUtils.toString(regResp.getEntity());
            if(sl.getStatusCode() == HttpStatus.SC_CREATED) {
                RegisteredApp app = serializer.fromJson(json, RegisteredApp.class);
                result.setResource(app);
            } 
            else if (json != null){
                Error error = serializer.fromJson(json, Error.class);
                result.setError(error);
            }
        }
        catch(Exception e) {
            LOG.warn("", e);
            result.setHttpCode(-1);
            Error err = new Error();
            
            err.setDetail(e.getMessage());
            result.setError(err);
        }
        
        return result;
    }
    
    private String getEndpoint(Class resClas) {
        String ep = classEndpointMap.get(resClas);
        if (ep == null) {
            throw new IllegalArgumentException("There is no endpoint found with the given resource class. Resource class must be registered to avoid this error");
        }
        
        return ep;
    }
    
    private void setIfNoneMatch(HttpRequestBase req, String ifNoneMatch) {
        if(ifNoneMatch != null) {
            req.setHeader("If-None-Match", ifNoneMatch);
        }
    }
    
    private <T> T unmarshal(String json, Class<T> resClass) throws Exception {
        JsonElement je = parser.parse(json);
        if(!(je instanceof JsonObject)) {
            return (T) je;
        }
        
        return unmarshal((JsonObject) je, resClass);
    }
    
    private <T> T unmarshal(JsonObject jsonObj, Class<T> resClass) throws Exception {
        T t = serializer.fromJson(jsonObj, resClass);
        Set<Field> extFields = endpointExtFieldMap.get(classEndpointMap.get(resClass));
        if (extFields != null) {
            for (Field f : extFields) {
                Extension ext = f.getAnnotation(Extension.class);
                String schemaId = ext.value();
                JsonElement je = jsonObj.get(schemaId);
                if(je != null) {
                    Object extObj = serializer.fromJson(je, f.getType());
                    f.set(t, extObj);
                }
            }
        }
        
        return t;
    }
}
