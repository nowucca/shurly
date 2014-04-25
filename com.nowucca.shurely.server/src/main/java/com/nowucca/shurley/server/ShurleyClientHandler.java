/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurley.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.URI;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

/**
 * Handles a client-side channel.
 */
public class ShurleyClientHandler extends SimpleChannelInboundHandler<ShurleyMessage> {

    private static final Logger logger = Logger.getLogger(
            ShurleyClientHandler.class.getName());

    private Map<URI,URI> long2short;

    public ShurleyClientHandler(Map<URI, URI> long2short) {
        this.long2short = long2short;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        logger.info(format("%s channel active", ctx.name()));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        logger.info(format("%s channel inactive", ctx.name()));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ShurleyMessage msg) throws Exception {
        // Print out the line received from the server.
        System.out.format("Received '%s'.\n", msg);
        if ( msg instanceof ShurleyShrunkMessage ) {
            ShurleyShrunkMessage m = (ShurleyShrunkMessage) msg;
            URI longURI = m.getLongURI();
            URI shortURI = m.getShortURI();
            System.out.format("Shortened '%s' to '%s'.\n", longURI, shortURI);
            long2short.put(longURI, shortURI);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.log(
                Level.WARNING,
                "Unexpected exception.",
                cause);
        ctx.channel().close();
    }
}
