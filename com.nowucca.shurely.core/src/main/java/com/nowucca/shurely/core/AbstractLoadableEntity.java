/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurely.core;

import com.nowucca.shurely.util.Config;

import java.util.Properties;

import static com.nowucca.shurely.util.ClassPathResourceUtil.loadClassProperties;
import static com.nowucca.shurely.util.ClassPathResourceUtil.loadPackageProperties;

public class AbstractLoadableEntity {

    private static final Properties EMPTY_PROPERTIES = new Properties();

    protected final Config config;

    protected AbstractLoadableEntity() {
        config = configure();
    }

    protected final Config getConfiguration() {
        return config;
    }

    private Config configure() {
        return Config.create(
                System.getProperties(),
                configure0(),
                loadClassProperties(getClass()),
                loadPackageProperties(getClass().getPackage()));
    }

    protected Properties configure0() {
        // configure your own configuration retrieval in here.
        return EMPTY_PROPERTIES;
    }


}
