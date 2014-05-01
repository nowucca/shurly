/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurely.core.context;

import com.nowucca.shurely.core.URIManager;

import java.util.Collection;

public interface URIManagerContext {

    URIManager getURIManager(String name);

    Collection<URIManager> getURIManagers();

    URIManager getSelectedURIManager();
}
