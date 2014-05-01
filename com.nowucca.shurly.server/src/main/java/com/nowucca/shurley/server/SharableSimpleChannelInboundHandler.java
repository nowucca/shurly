/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurley.server;

import io.netty.channel.SimpleChannelInboundHandler;

import static io.netty.channel.ChannelHandler.Sharable;

@Sharable
abstract class SharableSimpleChannelInboundHandler<T> extends SimpleChannelInboundHandler<T> {
}
