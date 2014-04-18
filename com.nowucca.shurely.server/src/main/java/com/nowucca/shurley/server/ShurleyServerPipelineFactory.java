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

import static org.jboss.netty.channel.Channels.*;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;

/**
 * Creates a newly configured {@link ChannelPipeline} for a new channel.
 */
public class ShurleyServerPipelineFactory implements
        ChannelPipelineFactory {

    public ChannelPipeline getPipeline() throws Exception {
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = pipeline();

        pipeline.addLast("commandDecoder", new ShurleyMessageDecoder());
        pipeline.addLast("commandEncoder", new ShurleyMessageEncoder());
        // and then business logic.
        pipeline.addLast("handler", new ShurleyServerHandler());

        return pipeline;
    }
}
