/**
 * Copyright (c) 2007-2012, Kaazing Corporation. All rights reserved.
 */
package com.nowucca.shurely.core.impl;

import com.nowucca.shurely.core.IntegerDrivenStringGenerator;
import com.nowucca.shurely.core.URIStore;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentMapURIStore implements URIStore {

    private ConcurrentHashMap<Integer, Record> database;
    private IntegerDrivenStringGenerator generator;

    public ConcurrentMapURIStore(IntegerDrivenStringGenerator generator) {
        this.database = new ConcurrentHashMap<Integer, Record>();
        this.generator = generator;
    }

    public URI putIfAbsent(URI longURI, URI shortURI) {
        Integer id = decodeURI(shortURI);
        Record record = new Record(id, longURI, shortURI);
        Record existingRecord = database.putIfAbsent(id, record);
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
        return generator.decode(shrunk.getPath().substring(1));
    }

    static class Record {
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
