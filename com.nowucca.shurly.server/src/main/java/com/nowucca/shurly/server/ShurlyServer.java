/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurly.server;

import com.nowucca.shurly.core.URIManager;
import com.nowucca.shurly.core.context.URIManagerContext;
import com.nowucca.shurly.core.context.URIManagerContextResolver;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.URI;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

/**
 * Simplistic url shortening server.
 */
public class ShurlyServer {

    private static final Logger logger = Logger.getLogger("server");

    private final int port;
    private final String uriManagerName;
    private URIManagerContext uriManagerContext;


    public ShurlyServer(int port, final String uriManagerName) {
        this.port = port;
        this.uriManagerName = uriManagerName;
    }

    public void run() throws Exception {
        final long start = System.currentTimeMillis();

        uriManagerContext =
                new URIManagerContextResolver().resolve(uriManagerName);

        printWelcomeMessage(uriManagerContext);

        final EventLoopGroup bossGroup = new NioEventLoopGroup();
        final EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // Configure the server.
            final ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(shurleyServerInitializer)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections.
            final ChannelFuture f = bootstrap.bind(port).sync();

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
        final Collection<URIManager> uriManagers = uriManagerContext.getURIManagers();
        for (URIManager uriManager: uriManagers) {
            System.out.format("\t %s\n", uriManager.getName());
        }
        System.out.format("\nUsing URI Manager: %s\n\n", uriManagerContext.getSelectedURIManager().getName());
        System.out.println();
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println(
                    "Usage: " + ShurlyServer.class.getSimpleName() +
                            " <port> <uri-manager-class>");
            System.err.println(
                    "  e.g. " + ShurlyServer.class.getSimpleName() +
                            " 8080 com.nowucca.shurly.core.basic.BasicURIManager");
            return;
        }

        new ShurlyServer(Integer.parseInt(args[0]), args[1]).run();
    }

    /**
     * Handles a server-side channel.
     */
   private final SharableSimpleChannelInboundHandler<ShurlyMessage> shurleyServerHandler
            = new SharableSimpleChannelInboundHandler<ShurlyMessage>() {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            logger.info(format("client arrival (%s)", ctx.channel().remoteAddress().toString()));
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            logger.info(format("client departure (%s)", ctx.channel().remoteAddress().toString()));
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ShurlyMessage msg) throws Exception {

            switch (msg.getKind()) {
                case SHRINK: {
                    handleShrinkRequest(ctx, msg);
                    break;
                }
                case FOLLOW: {
                    handleFollowRequest(ctx, msg);
                    break;
                }
                case SHRUNK:
                case ERROR:
                    logger.info(format("Ignoring an unexpectedly received %s message.", msg.getKind().toString()));
                    break;
                default:
                    throw new RuntimeException(String.format("Unrecognized message: %s", msg));
            }

        }

        private void handleFollowRequest(final ChannelHandlerContext ctx, final ShurlyMessage request) {
            try {
                final ShurlyFollowMessage followMsg = (ShurlyFollowMessage) request;

                logger.info(format("Received %s.v%d (%d) %s",
                        followMsg.getKind().name(),
                        followMsg.getVersion(),
                        followMsg.getMsgId(),
                        followMsg.getShortURI()));

                final URI shortURI = followMsg.getShortURI();
                final URIManager selectedURIManager = uriManagerContext.getSelectedURIManager();
                final ShurlyShrunkMessage response = new ShurlyShrunkMessage(
                        request.getVersion(), request.getMsgId(), selectedURIManager.follow(shortURI), shortURI);
                ctx.channel().writeAndFlush(response).addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            handleErrorResponseCondition(ctx, request, future.cause());
                        } else {
                            logger.info(format("Sent %s.v%d (%d) shortURI=%s longURI=%s",
                                    response.getKind().name(),
                                    response.getVersion(),
                                    response.getMsgId(),
                                    response.getShortURI(),
                                    response.getLongURI()));
                        }
                    }
                });
            } catch (Exception ex) {
                handleErrorResponseCondition(ctx, request, ex);
            }
        }

        private void handleShrinkRequest(final ChannelHandlerContext ctx, final ShurlyMessage request) {
            try {
                final ShurlyShrinkMessage shrinkMsg = (ShurlyShrinkMessage) request;

                logger.info(format("Received %s.v%d (%d) %s",
                        shrinkMsg.getKind().name(),
                        shrinkMsg.getVersion(),
                        shrinkMsg.getMsgId(),
                        shrinkMsg.getLongURI()));

                final URI longURI = shrinkMsg.getLongURI();
                final URIManager selectedURIManager = uriManagerContext.getSelectedURIManager();
                final ShurlyShrunkMessage response = new ShurlyShrunkMessage(
                        request.getVersion(), request.getMsgId(), longURI, selectedURIManager.shrink(longURI));
                ctx.channel().writeAndFlush(response).addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            handleErrorResponseCondition(ctx, request, future.cause());
                        } else {
                            logger.info(format("Sent %s.v%d (%d) shortURI=%s longURI=%s",
                                                                response.getKind().name(),
                                                                response.getVersion(),
                                                                response.getMsgId(),
                                                                response.getShortURI(),
                                                                response.getLongURI()));
                        }
                    }
                });
            } catch (Exception ex) {
                handleErrorResponseCondition(ctx, request, ex);
            }
        }

        private void handleErrorResponseCondition(ChannelHandlerContext ctx,
                                                  ShurlyMessage request,
                                                  final Throwable ex) {
            final ShurlyErrorMessage response = new ShurlyErrorMessage(
                    request.getVersion(), request.getMsgId(), ShurlyErrorCode.UNKNOWN_ERROR);
            ctx.channel().writeAndFlush(response).addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        logger.log(Level.SEVERE,
                                "Failed to send an error message to client (original exception=" +
                                        ex.getLocalizedMessage() + ")", future.cause());
                    } else {
                        logger.info(format("Sent %s.v%d (%d) errorCode=%d reason=\"%s\"",
                                                            response.getKind().name(),
                                                            response.getVersion(),
                                                            response.getMsgId(),
                                                            response.getErrorCode(),
                                                            response.getReason()));
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
            final ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast("commandDecoder", new ShurleyMessageDecoder());
            pipeline.addLast("commandEncoder", new ShurleyMessageEncoder());
            // and then business logic.
            pipeline.addLast("handler", shurleyServerHandler);
        }
    };
}
