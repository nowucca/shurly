/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurly.core;

import com.nowucca.shurly.util.Config;

import java.util.Properties;

import static com.nowucca.shurly.util.ClassPathResourceUtil.loadClassProperties;
import static com.nowucca.shurly.util.ClassPathResourceUtil.loadPackageProperties;

/**
 * An object that automatically loads properties as a {@link Config} object to configure itself, by default using
 * files in <code>META-INF/config/fully/qualified/package/name/className.properties</code> and
 * <code>META-INF/config/fully/qualified/package/name/packageName.properties</code>.
 *
 * Subclasses are able to provide properties by over-riding the
 * {@link #configure0()} method.
 *
 * When considering the order of property priority, the following sources are used, from highest to lowest priority:
 * <ol>
 *     <li>System properties</li>
 *     <li>Properties defined at runtime in a subclass using {@link #configure0()}</li>
 *     <li>Properties defined in <code>META-INF/config/fully/qualified/package/name/className.properties</code></li>
 *     <li>Properties defined in <code>META-INF/config/fully/qualified/package/name/packageName.properties</code></li>
 * </ol>
 */
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
