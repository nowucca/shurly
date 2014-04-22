/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurely.core.impl;

import com.nowucca.shurely.core.context.DefaultURIManagerContext;
import com.nowucca.shurely.core.context.URIManagerContext;
import com.nowucca.shurely.core.context.URIManagerContextResolver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

public class ConcurrentMapURIStoreTest {

    DefaultURIManagerContext context;

    @Before
    public void setUp() throws Exception {
        URIManagerContextResolver resolver = new URIManagerContextResolver();
        context = (DefaultURIManagerContext) resolver.resolve();
    }

    @Test
    public void testResourceInjections() throws Exception {
        ConcurrentMapURIStore store = (ConcurrentMapURIStore) context.getURIStore(ConcurrentMapURIStore.NAME);
        Assert.assertThat(store.getGenerator(), not(nullValue()));
    }
}
