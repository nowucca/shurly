/**
 * Copyright (c) 2013-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurly.core;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A URI Store with an in-memory representation for the association between long and short URIs.
 *
 * The {@link StringGenerator} is abstracted so this in memory class can be used with any integer driven generator.
 */
public abstract class AbstractInMemoryURIStore extends AbstractLoadableEntity implements URIStore {

    private ConcurrentHashMap<Integer, Record> database;
    private ConcurrentHashMap<URI, URI> long2short;

    protected AbstractInMemoryURIStore() {
        this.database = new ConcurrentHashMap<Integer, Record>(config.getInteger("mapCapacity", 16));
        long2short = new ConcurrentHashMap<URI, URI>(config.getInteger("mapCapacity", 16));

    }

    protected abstract AbstractIntegerDrivenStringGenerator getStringGenerator();

    public String getName() {
        return this.getClass().getCanonicalName();
    }

    @Override
    public boolean containsKey(URI longURI) {
        return long2short.containsKey(longURI);
    }

    @Override
    public URI get(URI longURI) {
        return long2short.get(longURI);
    }

    public URI putIfAbsent(URI longURI, URI shortURI) {
        URI result = null;
        final Integer id = decodeURI(shortURI);
        final Record record = new Record(id, longURI, shortURI);
        final Record existingRecord = database.putIfAbsent(id, record);
        if (existingRecord != null) {
            result = existingRecord.getShortURI();
        } else {
            final URI existingURI = long2short.putIfAbsent(longURI, shortURI);
            if (existingURI != null) {
                result = existingURI;
            }
        }
        return result;
    }

    public URI retrieve(URI shortURI) {
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
