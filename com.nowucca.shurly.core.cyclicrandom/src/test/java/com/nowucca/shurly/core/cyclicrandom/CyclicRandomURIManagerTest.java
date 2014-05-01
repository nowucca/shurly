/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurly.core.cyclicrandom;

import com.nowucca.shurly.core.context.URIManagerContext;
import com.nowucca.shurly.core.context.URIManagerContextResolver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

public class CyclicRandomURIManagerTest {

    URIManagerContext context;

    @Before
    public void setUp() throws Exception {
        URIManagerContextResolver resolver = new URIManagerContextResolver();
        context = resolver.resolve("com.nowucca.shurly.core.cyclicrandom.CyclicRandomURIManager");
    }

    @Test
    public void testResourceInjections() throws Exception {
        CyclicRandomURIManager manager = (CyclicRandomURIManager) context.getURIManager(CyclicRandomURIManager.class.getCanonicalName());

        Assert.assertThat(manager.getGenerator(), not(nullValue()));
        Assert.assertThat(manager.getGenerator(), instanceOf(CyclicRandomStringGenerator.class));

        Assert.assertThat(manager.getStore(), not(nullValue()));
        Assert.assertThat(manager.getStore(), instanceOf(CycleRandomInMemoryURIStore.class));
    }

    @Test
    public void shouldUseClassConfiguredDomainName() throws Exception {
        CyclicRandomURIManager manager = (CyclicRandomURIManager) context.getURIManager(CyclicRandomURIManager.class.getCanonicalName());

        final URI longURI = URI.create("http://www.google.com");
        final URI expectedShortURI = URI.create("http://nowucca.com/shurley/cyclicrandom/dRUPyN");
        URI shortURI = manager.shrink(longURI);
        Assert.assertEquals("failed to shrink to expected short uri", expectedShortURI, shortURI);
    }


}
