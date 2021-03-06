/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurly.core.base32;

import com.nowucca.shurly.core.context.URIManagerContext;
import com.nowucca.shurly.core.context.URIManagerContextResolver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

public class Base32URIManagerTest {

    URIManagerContext context;

    @Before
    public void setUp() throws Exception {
        URIManagerContextResolver resolver = new URIManagerContextResolver();
        context = resolver.resolve(Base32URIManager.NAME);
    }

    @Test
    public void testResourceInjections() throws Exception {
        Base32URIManager manager = (Base32URIManager) context.getURIManager(Base32URIManager.NAME);

        Assert.assertThat(manager.getGenerator(), not(nullValue()));
        Assert.assertThat(manager.getGenerator(), instanceOf(Base32StringGenerator.class));

        Assert.assertThat(manager.getStore(), not(nullValue()));
        Assert.assertThat(manager.getStore(), instanceOf(Base32InMemoryURIStore.class));
    }
}
