/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurley.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.net.URI;
import java.util.Map;

/**
 * Creates a newly configured {@link ChannelPipeline} for a new channel.
 */
public class ShurelyClientChannelInitializer extends
        ChannelInitializer<SocketChannel> {

    private Map<URI,URI> long2short;

    public ShurelyClientChannelInitializer(Map<URI, URI> long2short) {
        this.long2short = long2short;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = ch.pipeline();

        // Add the codec
        pipeline.addLast("decoder", new ShurleyMessageDecoder());
        pipeline.addLast("encoder", new ShurleyMessageEncoder());

        // and then business logic.
        pipeline.addLast("handler", new ShurleyClientHandler(long2short));
    }
}
