/*
 * Copyright (c) 2016 Keydap Software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * See LICENSE file for details.
 */
package com.keydap.sparrow;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * RFC3339 date serializer
 * 
 * @author Kiran Ayyagari (kayyagari@keydap.com)
 */
public class DateTimeSerializer implements JsonSerializer<Date>, JsonDeserializer<Date> {

    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");//RFC3339
    
    public DateTimeSerializer() {
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    
    @Override
    public Date deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
        String d = json.getAsString();
        try {
            return df.parse(d);
        }
        catch(Exception e) {
            throw new JsonParseException(e);
        }
    }

    @Override
    public JsonElement serialize(Date src, Type typeOfSrc,
            JsonSerializationContext context) {
        String value = df.format(src);
        return new JsonPrimitive(value);
    }

}
