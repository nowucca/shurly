/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.nowucca.shurley.server;

import java.net.URI;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 * Handles a client-side channel.
 */
public class ShurleyClientHandler extends SimpleChannelUpstreamHandler {

    private static final Logger logger = Logger.getLogger(
            ShurleyClientHandler.class.getName());

    private Map<URI,URI> long2short;

    public ShurleyClientHandler(Map<URI, URI> long2short) {
        this.long2short = long2short;
    }

    @Override
    public void handleUpstream(
            ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof ChannelStateEvent) {
            logger.info(e.toString());
        }
        super.handleUpstream(ctx, e);
    }

    @Override
    public void messageReceived(
            ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        // Print out the line received from the server.
        System.out.println(String.format("Received '%s'.", e.getMessage()));
        if ( e.getMessage() instanceof ShurleyShrunkMessage ) {
            ShurleyShrunkMessage m = (ShurleyShrunkMessage) e.getMessage();
            URI longURI = m.getLongURI();
            URI shortURI = m.getShortURI();
            System.out.println(String.format("Shortened '%s' to '%s'.", longURI, shortURI));
            long2short.put(longURI, shortURI);
        }
        super.messageReceived(ctx, e);
    }

    @Override
    public void exceptionCaught(
            ChannelHandlerContext ctx, ExceptionEvent e) {
        logger.log(
                Level.WARNING,
                "Unexpected exception from downstream.",
                e.getCause());
        e.getChannel().close();
    }
}
