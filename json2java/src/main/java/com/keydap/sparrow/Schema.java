/*
 * Copyright (c) 2016 Keydap Software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * See LICENSE file for complete details.
 */
package com.keydap.sparrow;

import java.util.List;

/**
 *
 * @author Kiran Ayyagari (kayyagari@keydap.com)
 */
public class Schema {
    String id;
    String name;
    String description;
    
    List<AttributeType> attributes;
}
