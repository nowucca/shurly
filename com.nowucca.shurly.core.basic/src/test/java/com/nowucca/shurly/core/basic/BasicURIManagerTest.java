/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurly.core.basic;

import com.nowucca.shurly.core.context.URIManagerContext;
import com.nowucca.shurly.core.context.URIManagerContextResolver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

public class BasicURIManagerTest {

    URIManagerContext context;

    @Before
    public void setUp() throws Exception {
        URIManagerContextResolver resolver = new URIManagerContextResolver();
        context = resolver.resolve("com.nowucca.shurly.core.basic.BasicURIManager");
    }

    @Test
    public void testResourceInjections() throws Exception {
        com.nowucca.shurly.core.basic.BasicURIManager manager = (com.nowucca.shurly.core.basic.BasicURIManager) context.getURIManager("com.nowucca.shurly.core.basic.BasicURIManager");

        Assert.assertThat(manager.getGenerator(), not(nullValue()));
        Assert.assertThat(manager.getGenerator(), instanceOf(BasicSequentialAlphabetStringGenerator.class));

        Assert.assertThat(manager.getStore(), not(nullValue()));
        Assert.assertThat(manager.getStore(), instanceOf(BasicInMemoryURIStore.class));
    }
}
