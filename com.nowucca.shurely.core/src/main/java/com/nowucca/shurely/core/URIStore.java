/**
 * Copyright (c) 2007-2012, Kaazing Corporation. All rights reserved.
 */
package com.nowucca.shurely.core;

import java.net.URI;

public interface URIStore extends NamedObject {

    URI putIfAbsent(URI longURI, URI shortURI);

    URI get(URI shortURI);
}
