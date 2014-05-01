/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurley.server;

import com.nowucca.shurely.core.URIManager;
import com.nowucca.shurely.core.basic.BasicURIManager;
import com.nowucca.shurely.core.context.URIManagerContext;
import com.nowucca.shurely.core.context.URIManagerContextResolver;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import javax.annotation.Resource;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.nowucca.shurely.util.ResourceInjectionUtil.inject;
import static java.lang.String.format;

/**
 * Simplistic url shortening server.
 */
public class ShurelyServer {

    private static final Logger logger = Logger.getLogger("server");

    private final int port;
    private final String uriManagerName;
    private URIManagerContext uriManagerContext;


    public ShurelyServer(int port, final String uriManagerName) {
        this.port = port;
        this.uriManagerName = uriManagerName;
    }

    public void run() throws Exception {
        long start = System.currentTimeMillis();

        uriManagerContext =
                new URIManagerContextResolver().resolve(uriManagerName);

        printWelcomeMessage(uriManagerContext);

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // Configure the server.
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(shurleyServerInitializer)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            ChannelFuture f = bootstrap.bind(port).sync();

            System.out.format("Started in %3.3f seconds.\n", (System.currentTimeMillis() - start) / 1000f);

            f.channel().closeFuture().sync();

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }


    private void printWelcomeMessage(URIManagerContext uriManagerContext) {
        System.out.format("Shurley Server (c) 2014 Steven Atkinson.  All Rights Reserved.\n\n");
        System.out.format("Available URI Managers are: \n");
        Collection<URIManager> uriManagers = uriManagerContext.getURIManagers();
        for(URIManager uriManager: uriManagers) {
            System.out.format("\t %s\n", uriManager.getName());
        }
        System.out.format("\nUsing URI Manager: %s\n\n", uriManagerContext.getSelectedURIManager().getName());
        System.out.println();
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println(
                    "Usage: " + ShurelyServer.class.getSimpleName() +
                            " <port> <uri-manager-class>");
            System.err.println(
                    "  e.g. " + ShurelyServer.class.getSimpleName() +
                            " 8080 com.nowucca.shurley.core.basic.BasicURIManager");
            return;
        }

        new ShurelyServer(Integer.parseInt(args[0]), args[1]).run();
    }

    /**
     * Handles a server-side channel.
     */
   private final SharableSimpleChannelInboundHandler<ShurleyMessage> shurleyServerHandler
            = new SharableSimpleChannelInboundHandler<ShurleyMessage>() {

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

        private void handleFollowRequest(final ChannelHandlerContext ctx, final ShurleyMessage request) {
            try {
                final URI shortURI = ((ShurleyFollowMessage) request).getShortURI();
                final URIManager selectedURIManager = uriManagerContext.getSelectedURIManager();
                ShurleyShrunkMessage response = new ShurleyShrunkMessage(
                        request.getVersion(), request.getMsgId(), selectedURIManager.follow(shortURI), shortURI);
                ctx.channel().writeAndFlush(response).addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            handleErrorResponseCondition(ctx, request, future.cause());
                        }
                    }
                });
            } catch (Exception ex) {
                handleErrorResponseCondition(ctx, request, ex);
            }
        }

        private void handleShrinkRequest(final ChannelHandlerContext ctx, final ShurleyMessage request) {
            try {
                final URI longURI = ((ShurleyShrinkMessage) request).getLongURI();
                final URIManager selectedURIManager = uriManagerContext.getSelectedURIManager();
                ShurleyShrunkMessage response = new ShurleyShrunkMessage(
                        request.getVersion(), request.getMsgId(), longURI, selectedURIManager.shrink(longURI));
                ctx.channel().writeAndFlush(response).addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            handleErrorResponseCondition(ctx, request, future.cause());
                        }
                    }
                });
            } catch (Exception ex) {
                handleErrorResponseCondition(ctx, request, ex);
            }
        }

        private void handleErrorResponseCondition(ChannelHandlerContext ctx, ShurleyMessage request, final Throwable ex) {
            ShurleyErrorMessage errorMessage = new ShurleyErrorMessage(
                    request.getVersion(), request.getMsgId(), ShurleyErrorCode.UNKNOWN_ERROR);
            ctx.channel().writeAndFlush(errorMessage).addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(!future.isSuccess()) {
                        logger.log(Level.SEVERE,
                                "Failed to send an error message to client (original exception="+ex.getLocalizedMessage()+")", future.cause());
                    }
                }
            });
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
    };

    /**
     * Creates a newly configured {@link io.netty.channel.ChannelPipeline} for a new channel.
     */
    private final ChannelInitializer<SocketChannel> shurleyServerInitializer = new
            ChannelInitializer<SocketChannel>() {

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            // Create a default pipeline implementation.
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast("commandDecoder", new ShurleyMessageDecoder());
            pipeline.addLast("commandEncoder", new ShurleyMessageEncoder());
            // and then business logic.
            pipeline.addLast("handler", shurleyServerHandler);
        }
    };
}
