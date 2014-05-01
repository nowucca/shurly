/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurely.util;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Config {
    
    List<Properties> propertiesList;

    private Config(List<Properties> propertieses) {
        this.propertiesList = propertieses;
    }

    public static Config create(Properties... props) {
        return new Config(Arrays.asList(props));        
    }

    public Boolean getBoolean(String name) {
        return getBoolean(name, null);
    }

    
    public Boolean getBoolean(String name, Boolean defaultValue) {
        Boolean result = defaultValue;
        String stringResult = getString(name);
        if (stringResult != null) {
            result = Boolean.valueOf(stringResult);
        } else {
            result = defaultValue;
        }
        return result;
    }

    public Long getLong(String name) {
        return getLong(name, null);
    }
    
    public Long getLong(String name, Long defaultValue) {
        Long result = defaultValue;
        String stringResult = getString(name);
        if (stringResult != null) {
            result = Long.valueOf(stringResult);
        } else {
            result = defaultValue;
        }
        return result;
    }

    public Integer getInteger(String name) {
        return getInteger(name, null);
    }
    
    public Integer getInteger(String name, Integer defaultValue) {
        Integer result = defaultValue;
        String stringResult = getString(name);
        if (stringResult != null) {
            result = Integer.valueOf(stringResult);
        } else {
            result = defaultValue;
        }
        return result;
    }
    
    public byte[] getByteArray(String name) {
            return getByteArray(name, null);
        }

    public byte[] getByteArray(String name, byte[] defaultValue) {
        byte[] result = defaultValue;
        String stringResult = getString(name);
        if (stringResult != null) {
            result = stringResult.getBytes(UTF8.charset());
        } else {
            result = defaultValue;
        }
        return result;
    }

    public String getString(String name) {
        return getString(name, null);
    }

    public String getString(String name, String defaultValue) {
        String result = defaultValue;
        if (propertiesList != null) {
            for(Properties p: propertiesList) {
                String property = p.getProperty(name);
                if (property != null) {
                    result = property;
                    break;
                }
            }
        }
        return result;
    }
    
    
}
