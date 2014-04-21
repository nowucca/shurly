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
import java.util.logging.Level;
import java.util.logging.Logger;


import com.nowucca.shurely.core.impl.BasicURIManager;
import com.nowucca.shurely.core.URIManager;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 * Handles a server-side channel.
 */
public class ShurleyServerHandler extends SimpleChannelUpstreamHandler {

    private static final Logger logger = Logger.getLogger(
            ShurleyServerHandler.class.getName());

    private URIManager manager = new BasicURIManager();


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
            ChannelHandlerContext ctx, MessageEvent e) {

        ShurleyMessage request = (ShurleyMessage) e.getMessage();

        switch (request.getKind()) {
            case SHRINK: {
                handleShrinkRequest(e, request);
                break;
            }
            case FOLLOW: {
                handleFollowRequest(e, request);
                break;
            }
            default:
                throw new Error("Should not get here.");
        }

    }

    private void handleFollowRequest(MessageEvent e, ShurleyMessage request) {
        try {
            final URI shortURI = ((ShurleyFollowMessage) request).getShortURI();
            ShurleyShrunkMessage response = new ShurleyShrunkMessage(
                    request.getVersion(), request.getMsgId(), manager.follow(shortURI), shortURI);
            e.getChannel().write(response);
        } catch (Exception ex) {
            handleErrorResponseCondition(e, request, ex);
        }
    }

    private void handleShrinkRequest(MessageEvent e, ShurleyMessage request) {
        try {
            final URI longURI = ((ShurleyShrinkMessage) request).getLongURI();
            ShurleyShrunkMessage response = new ShurleyShrunkMessage(
                    request.getVersion(), request.getMsgId(), longURI, manager.shrink(longURI));
            e.getChannel().write(response);
        } catch (Exception ex) {
            handleErrorResponseCondition(e, request, ex);
        }
    }

    private void handleErrorResponseCondition(MessageEvent e, ShurleyMessage request, Exception ex) {
        ShurleyErrorMessage errorMessage = new ShurleyErrorMessage(
                request.getVersion(), request.getMsgId(), ShurleyErrorCode.UNKNOWN_ERROR);
        e.getChannel().write(errorMessage);
        final RuntimeException runtimeException = new RuntimeException();
        runtimeException.initCause(ex);
        throw runtimeException;
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
