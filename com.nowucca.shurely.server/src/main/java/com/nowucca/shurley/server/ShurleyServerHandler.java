/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurley.server;

import com.nowucca.shurely.core.URIManager;
import com.nowucca.shurely.core.basic.BasicURIManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

/**
 * Handles a server-side channel.
 */
public class ShurleyServerHandler extends SimpleChannelInboundHandler<ShurleyMessage> {

    private static final Logger logger = Logger.getLogger(
            ShurleyServerHandler.class.getName());

    private URIManager manager = new BasicURIManager();


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

        switch (msg.getKind()) {
            case SHRINK: {
                handleShrinkRequest(ctx, msg);
                break;
            }
            case FOLLOW: {
                handleFollowRequest(ctx, msg);
                break;
            }
            default:
                throw new Error("Should not get here.");
        }

    }

    private void handleFollowRequest(ChannelHandlerContext ctx, ShurleyMessage request) {
        try {
            final URI shortURI = ((ShurleyFollowMessage) request).getShortURI();
            ShurleyShrunkMessage response = new ShurleyShrunkMessage(
                    request.getVersion(), request.getMsgId(), manager.follow(shortURI), shortURI);
            ctx.channel().writeAndFlush(response);
        } catch (Exception ex) {
            handleErrorResponseCondition(ctx, request, ex);
        }
    }

    private void handleShrinkRequest(ChannelHandlerContext ctx, ShurleyMessage request) {
        try {
            final URI longURI = ((ShurleyShrinkMessage) request).getLongURI();
            ShurleyShrunkMessage response = new ShurleyShrunkMessage(
                    request.getVersion(), request.getMsgId(), longURI, manager.shrink(longURI));
            ctx.channel().writeAndFlush(response);
        } catch (Exception ex) {
            handleErrorResponseCondition(ctx, request, ex);
        }
    }

    private void handleErrorResponseCondition(ChannelHandlerContext ctx, ShurleyMessage request, Exception ex) {
        ShurleyErrorMessage errorMessage = new ShurleyErrorMessage(
                request.getVersion(), request.getMsgId(), ShurleyErrorCode.UNKNOWN_ERROR);
        ctx.channel().writeAndFlush(errorMessage);
        final RuntimeException runtimeException = new RuntimeException();
        runtimeException.initCause(ex);
        throw runtimeException;
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
