/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurely.core.impl;

import com.nowucca.shurely.core.URIManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ConcurrentHashMap;

public class Base32URIManager implements URIManager {

    Base32StringGenerator generator = new Base32StringGenerator();

    public static final String DOMAIN="shure.ly";

    private ConcurrentHashMap<Integer,Record> database;

    public Base32URIManager() {
        database = new ConcurrentHashMap<Integer, Record>();
    }

    public URI shrink(URI longURI) {
        if ( longURI == null ) {
            throw new NullPointerException("longURI");
        }
        URI shrunk =  makeShortening(longURI);
        Integer id = decodeURI(shrunk);
        Record record = new Record(id, longURI, shrunk);
        Record existing = database.putIfAbsent(id, record);
        if ( existing != null ) {
            shrunk = existing.getShortURI();
        }
        return shrunk;
    }

    public URI follow(URI shortURI) {
        if ( shortURI == null ) {
            throw new NullPointerException("shortURI");
        }

        final int id = decodeURI(shortURI);
        return database.get(id).getLongURI();
    }

    private static class Record {
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

    private int decodeURI(URI shrunk) {
        return generator.decode(shrunk.getPath().substring(1));
    }


    private URI makeShortening(URI sourceURI) {
        try {
            return new URI(sourceURI.getScheme(), DOMAIN, "/"+generator.get(), null);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to shorten URI: "+sourceURI, e);
        }
    }


}
