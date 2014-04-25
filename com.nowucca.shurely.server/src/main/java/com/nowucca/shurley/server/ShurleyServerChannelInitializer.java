/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurley.server;

import com.nowucca.shurely.core.context.URIManagerContext;
import com.nowucca.shurely.util.ResourceInjectionUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.Map;

import static com.nowucca.shurely.util.ResourceInjectionUtil.inject;

/**
 * Creates a newly configured {@link ChannelPipeline} for a new channel.
 */
public class ShurleyServerChannelInitializer extends
        ChannelInitializer<SocketChannel> {

    private URIManagerContext uriManagerContext;
    public ShurleyServerChannelInitializer(URIManagerContext uriManagerContext) {
        this.uriManagerContext = uriManagerContext;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("commandDecoder", new ShurleyMessageDecoder());
        pipeline.addLast("commandEncoder", new ShurleyMessageEncoder());
        // and then business logic.
        final ShurleyServerHandler serverHandler = new ShurleyServerHandler();
        pipeline.addLast("handler", serverHandler);

        // Inject shared context into the channel handlers
        Iterator<Map.Entry<String,ChannelHandler>> iterator = pipeline.iterator();
        while (iterator.hasNext()) {
            inject(iterator.next().getValue(),
                    URIManagerContext.class, uriManagerContext);
        }
    }
}
