/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurely.core.basic;

import com.nowucca.shurely.core.context.URIManagerContext;
import com.nowucca.shurely.core.context.URIManagerContextResolver;
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
        context = resolver.resolve();
    }

    @Test
    public void testResourceInjections() throws Exception {
        com.nowucca.shurely.core.basic.BasicURIManager manager = (com.nowucca.shurely.core.basic.BasicURIManager) context.getURIManager("com.nowucca.shurely.core.basic.BasicURIManager");

        Assert.assertThat(manager.getGenerator(), not(nullValue()));
        Assert.assertThat(manager.getGenerator(), instanceOf(BasicSequentialAlphabetStringGenerator.class));

        Assert.assertThat(manager.getStore(), not(nullValue()));
        Assert.assertThat(manager.getStore(), instanceOf(BasicInMemoryURIStore.class));
    }
}
