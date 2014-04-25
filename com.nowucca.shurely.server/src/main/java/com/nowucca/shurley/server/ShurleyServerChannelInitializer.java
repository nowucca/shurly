/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurley.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * Creates a newly configured {@link ChannelPipeline} for a new channel.
 */
public class ShurleyServerChannelInitializer extends
        ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("commandDecoder", new ShurleyMessageDecoder());
        pipeline.addLast("commandEncoder", new ShurleyMessageEncoder());
        // and then business logic.
        pipeline.addLast("handler", new ShurleyServerHandler());
    }
}
