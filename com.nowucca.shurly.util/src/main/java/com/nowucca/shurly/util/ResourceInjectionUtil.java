/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurly.util;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public final class ResourceInjectionUtil {

    private ResourceInjectionUtil() {
    }

    @SuppressWarnings("unused")
    public static <T> void inject(Object target,
                                  Class<T> injectableType,
                                  T injectableInstance) {
        inject0(target, injectableType, injectableInstance);
    }

    public static void injectAll(Object target, Map<Class<?>, Object> injectables) {
           for (Map.Entry<Class<?>, Object> entry : injectables.entrySet()) {
               final Class<?> injectableType = entry.getKey();
               final Object injectableInstance = entry.getValue();
               if (target != injectableInstance) {
                   inject0(target, injectableType, injectableInstance);
               }
           }
       }


    private static void inject0(Object target,
            Class<?> injectableType,
            Object injectableInstance) {

        final Class<?> targetClass = target.getClass();
        final Method[] methods = targetClass.getMethods();
        for (Method method : methods) {
            final String methodName = method.getName();
            final Class<?>[] parameterTypes = method.getParameterTypes();
            if (methodName.startsWith("set") &&
                methodName.length() > "set".length() &&
                parameterTypes.length == 1) {

                final Resource annotation = method.getAnnotation(Resource.class);
                if (annotation != null) {
                    Class<?> resourceType = annotation.type();
                    if (resourceType == Object.class) {
                        resourceType = parameterTypes[0];
                    }
                    if (resourceType == injectableType) {
                        try {
                            method.invoke(target, injectableInstance);
                        } catch (IllegalArgumentException e) {
                            throw new RuntimeException(e);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        } catch (InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }
}
