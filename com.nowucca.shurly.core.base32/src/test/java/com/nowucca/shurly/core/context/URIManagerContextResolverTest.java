/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurly.core.context;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

public class URIManagerContextResolverTest {

    @Test
    public void testSimpleResolve() throws Exception {
        URIManagerContextResolver resolver = new URIManagerContextResolver();
        URIManagerContext context = resolver.resolve("com.nowucca.shurly.core.base32.Base32URIManager");

        Assert.assertThat(context.getURIManager("com.nowucca.shurly.core.base32.Base32URIManager"), not(nullValue()));
    }
}
