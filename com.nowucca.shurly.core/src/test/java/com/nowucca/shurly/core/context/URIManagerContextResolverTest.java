/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurly.core.context;

import org.junit.Test;

import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

public class URIManagerContextResolverTest {

    @Test(expected = IllegalArgumentException.class)
    public void testSimpleResolve() throws Exception {
        URIManagerContextResolver resolver = new URIManagerContextResolver();
        URIManagerContext context = resolver.resolve("com.nowucca.shurly.core.basic.BasicURIManager");
        // cannot resolve because no uri manager can be selected
    }
}
