/*
 * Copyright (c) 2016 Keydap Software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * See LICENSE file for complete details.
 */
package com.keydap.sparrow;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation for all the resource models.
 * 
 * @author Kiran Ayyagari (kayyagari@keydap.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Resource {
    /** the primary schema ID of this resource */
    String schemaId();

    /** the endpoint URI e.g /Users */
    String endpoint();
    
    /** the description of the resource */
    String desc() default "";
}
