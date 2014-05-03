/**
 * Copyright (c) 2013-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurly.core;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractInMemoryURIStore extends AbstractLoadableEntity implements URIStore {

    private ConcurrentHashMap<Integer, Record> database;

    protected AbstractInMemoryURIStore() {
        this.database = new ConcurrentHashMap<Integer, Record>(config.getInteger("mapCapacity", 16));
    }

    protected abstract AbstractIntegerDrivenStringGenerator getStringGenerator();

    public String getName() {
        return this.getClass().getCanonicalName();
    }

    public URI putIfAbsent(URI longURI, URI shortURI) {
        final Integer id = decodeURI(shortURI);
        final Record record = new Record(id, longURI, shortURI);
        final Record existingRecord = database.putIfAbsent(id, record);
        if (existingRecord != null) {
            return existingRecord.getShortURI();
        }
        return null;
    }

    public URI get(URI shortURI) {
        final int id = decodeURI(shortURI);
        return database.get(id).getLongURI();
    }

    private int decodeURI(URI shrunk) {
        return getStringGenerator().decode(shrunk.getPath().substring(1));
    }

    private static final class Record {
        int id;
        URI longURI;
        URI shortURI;

        private Record(int id, URI longURI, URI shortURI) {
            this.id = id;
            this.longURI = longURI;
            this.shortURI = shortURI;
        }

        public int getId() {
            return id;
        }

        public URI getLongURI() {
            return longURI;
        }

        public URI getShortURI() {
            return shortURI;
        }
    }
}
