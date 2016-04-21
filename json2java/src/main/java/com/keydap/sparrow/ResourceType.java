/*
 * Copyright (c) 2016 Keydap Software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * See LICENSE file for complete details.
 */
package com.keydap.sparrow;

import java.util.List;

import com.google.gson.Gson;

/**
 *
 * @author Kiran Ayyagari (kayyagari@keydap.com)
 */
public class ResourceType {
    List<String> schemas;
    String id;
    String name;
    String endpoint;
    String description;
    String schema;
    List<Extension> schemaExtensions;

    static class Extension {
        String schema;
        boolean required;

        public String getSchema() {
            return schema;
        }

        public boolean isRequired() {
            return required;
        }

    }

    public static void main(String[] args) {
        String json = "{\"schemas\":[\"urn:ietf:params:scim:schemas:core:2.0:ResourceType\"],\"id\":\"Group\",\"name\":\"Group\",\"endpoint\":\"/Groups\",\"description\":\"Group\",\"schema\":\"urn:ietf:params:scim:schemas:core:2.0:Group\",\"meta\":{\"location\":\"v2/ResourceTypes/Group\",\"resourceType\":\"ResourceType\"}}";
        json = "{\"schemas\":[\"urn:ietf:params:scim:schemas:core:2.0:ResourceType\"],\"id\":\"User\",\"name\":\"User\",\"endpoint\":\"/Users\",\"description\":\"UserAccount\",\"schema\":\"urn:ietf:params:scim:schemas:core:2.0:User\",\"schemaExtensions\":[{\"schema\":\"urn:ietf:params:scim:schemas:extension:enterprise:2.0:User\",\"required\":false}],\"meta\":{\"location\":\"v2/ResourceTypes/User\",\"resourceType\":\"ResourceType\"}}";
//        System.out.println(json);
        ResourceType rt = new Gson().fromJson(json, ResourceType.class);
        
        System.out.println(rt);
    }
}
