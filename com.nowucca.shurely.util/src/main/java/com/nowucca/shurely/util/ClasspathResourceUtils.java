/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurely.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Help for loading things from the classpath.
 */
public class ClassPathResourceUtils {

    /**
     * Class properties come from the file <code>META-INF/config/full/package/name/{SimpleClassName}.properties</code>
     * @param clazz the class for which to load the properties
     * @return a non-null Properties object containing class-specific properties
     */
    public static Properties loadClassProperties(Class<?> clazz) {
        final String packageName = clazz.getPackage().getName();
        return loadResourceAsProperties(String.format("META-INF/config/%s/%s.%s",
                packageName.replace('.', '/'),
                clazz.getSimpleName(),
                "properties"));
    }

    /**
     * Package properties come from the file <code>META-INF/config/full/package/name/config.properties</code>
     * @param pkg the package for which we need to load properties
     * @return a non-null Properties object containing package-specific properties
     */
    public static Properties loadPackageProperties(Package pkg) {
            final String packageName = pkg.getName();
            return loadResourceAsProperties(String.format("META-INF/config/%s/%s.%s",
                    packageName.replace('.', '/'),
                    "config",
                    "properties"));
        }


    private static Properties loadResourceAsProperties(String resourceLocation) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        BufferedReader in = null;
        Properties properties = new Properties();
        try {
            URL resource = classLoader.getResource(resourceLocation);
            in = new BufferedReader(new InputStreamReader(resource.openStream(), UTF8.charset()));
            properties.load(in);
        } catch (Exception e) {
            // TODO: log warning
        } finally {
            if (in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    //TODO: log warning
                }
            }
        }
        return properties;

    }

}
