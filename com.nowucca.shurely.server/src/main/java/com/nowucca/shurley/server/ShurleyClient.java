/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurley.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;

/**
 * Simplistic telnet-style Shurley client.
 */
public class ShurleyClient {

    private static final short VERSION = (short) 1;
    private final String host;
    private final int port;

    private Map<URI, URI> long2short = new HashMap<URI, URI>();
    private static int msgId = 0;

    public ShurleyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() throws Exception {
        // Configure the client.
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(shurelyClientInitializer);

            // Start the connection attempt.
            ChannelFuture future = bootstrap.connect(host, port).sync();

            printWelcomeInstructions();

            // Read commands from the stdin.
            ChannelFuture lastWriteFuture = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            for (; ; ) {
                if (lastWriteFuture != null) {
                    lastWriteFuture.syncUninterruptibly();
                }

                String line = in.readLine();
                if (line == null) {
                    continue;
                }

                lastWriteFuture = processLine(line, future.channel());

                // If user typed the 'bye' command,  close
                // the connection and cleanup.
                if (line.toLowerCase().equals("bye")) {
                    future.channel().close().awaitUninterruptibly();
                    workerGroup.shutdownGracefully();
                    break;
                }
            }

            // Wait until all messages are flushed before closing the channel.
            if (lastWriteFuture != null) {
                lastWriteFuture.awaitUninterruptibly();
            }

            // Close the connection.  Make sure the close operation ends because
            // all I/O operations are asynchronous in Netty.
            future.channel().close().sync();

        } finally {
            workerGroup.shutdownGracefully();
        }
    }



    private ChannelFuture processLine(String line, Channel channel) {

        if (line.startsWith("shrink ")) {
            URI longURI = readUriFromLine(line, "shrink ");
            if (longURI == null) return null;
            ShurleyShrinkMessage msg = new ShurleyShrinkMessage(VERSION, msgId++, longURI);
            return channel.writeAndFlush(msg);
        } else if (line.startsWith("follow ")) {
            URI longURI = readUriFromLine(line, "follow ");
            if (longURI == null) return null;
            ShurleyFollowMessage msg = new ShurleyFollowMessage(VERSION, msgId++, longURI);
            return channel.writeAndFlush(msg);
        } else if (line.startsWith("list")) {
            System.out.println();
            System.out.println();
            for (Map.Entry<URI, URI> shortening : long2short.entrySet()) {
                System.out.println(shortening.getKey() + " -> " + shortening.getValue());
            }
            System.out.println();
            printPrompt();
            return null;
        } else if (line.equals("bye")) {
            return null; // close handled in main loop
        } else {
            System.out.println(format("Unrecognized command '%s'.", line));
            printWelcomeInstructions();
            return null;
        }
    }

    private URI readUriFromLine(String line, final String command) {
        final String enteredURI = line.substring(command.length());
        URI longURI;
        try {
            longURI = new URI(enteredURI);
        } catch (URISyntaxException e) {
            System.out.println(format("Malformed URL: '%s'.", enteredURI));
            return null;
        }
        return longURI;
    }

    private static void printPrompt() {
        System.out.print("shurley> ");
    }

    private void printWelcomeInstructions() {
        System.out.println("Available commands: ");
        System.out.println("  shrink <uri>      -- shrinks the uri provided");
        System.out.println("  follow <uri>      -- follows the uri provided");
        System.out.println("  list              -- list the shortenings so far");
        System.out.println("  bye               -- quit");
        System.out.println();
        printPrompt();
    }

    public static void main(String[] args) throws Exception {
        // Print usage if no argument is specified.
        // Parse options.

        if (args.length != 2) {
            System.err.println(
                    "Usage: " + ShurleyClient.class.getSimpleName() +
                    " <host> <port>");
            return;
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);

        new ShurleyClient(host, port).run();
    }

    /**
     * Handles a client-side channel.
     */
    private final SimpleChannelInboundHandler<ShurleyMessage> shurleyClientHandler = new SimpleChannelInboundHandler<ShurleyMessage>() {

        private AttributeKey<Map<URI,URI>> CLIENT_CACHE = AttributeKey.valueOf("shurleyClientHandler.CLIENT_CACHE");

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
           ctx.channel().attr(CLIENT_CACHE).set(new ConcurrentHashMap<URI, URI>());
           super.channelRegistered(ctx);
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ShurleyMessage msg) throws Exception {
            // Print out the line received from the server.
            System.out.format("Received %s.v%d (#%d)\n", msg.getKind(), msg.getVersion(), msg.getMsgId());
            if ( msg instanceof ShurleyShrunkMessage ) {
                ShurleyShrunkMessage m = (ShurleyShrunkMessage) msg;
                URI longURI = m.getLongURI();
                URI shortURI = m.getShortURI();
                System.out.format("longURI=\"%s\" shortURI=\"%s\".\n", longURI, shortURI);
                ctx.channel().attr(CLIENT_CACHE).get().put(longURI, shortURI);
            }
            printPrompt();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            System.out.println("Unexpected exception.");
            cause.printStackTrace(System.out);
            printPrompt();
        }
    };

    /**
     * Creates a newly configured {@link io.netty.channel.ChannelPipeline} for a new channel.
     */
    private final ChannelInitializer<SocketChannel> shurelyClientInitializer = new
            ChannelInitializer<SocketChannel>() {

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            // Create a default pipeline implementation.
            ChannelPipeline pipeline = ch.pipeline();

            // Add the codec
            pipeline.addLast("decoder", new ShurleyMessageDecoder());
            pipeline.addLast("encoder", new ShurleyMessageEncoder());

            // and then business logic.
            pipeline.addLast("handler", shurleyClientHandler);
        }
    };
}
