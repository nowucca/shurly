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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

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

    public void run() throws IOException {
        // Configure the client.
        ClientBootstrap bootstrap = new ClientBootstrap(
                new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        // Configure the pipeline factory.
        bootstrap.setPipelineFactory(new ShurelyClientPipelineFactory(long2short));

        // Start the connection attempt.
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));

        // Wait until the connection attempt succeeds or fails.
        Channel channel = future.awaitUninterruptibly().getChannel();
        if (!future.isSuccess()) {
            future.getCause().printStackTrace();
            bootstrap.releaseExternalResources();
            return;
        }

        printWelcomeInstructions();

        // Read commands from the stdin.
        ChannelFuture lastWriteFuture = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        for (;;) {
            if (lastWriteFuture != null) {
                lastWriteFuture.awaitUninterruptibly();
            }
            printPrompt();
            
            String line = in.readLine();
            if (line == null) {
                break;
            }

            lastWriteFuture = processLine(line, channel);

            // If user typed the 'bye' command,  close
            // the connection and cleanup.
            if (line.toLowerCase().equals("bye")) {
                channel.close().awaitUninterruptibly();
                bootstrap.releaseExternalResources();
                return;
            }
        }

        // Wait until all messages are flushed before closing the channel.
        if (lastWriteFuture != null) {
            lastWriteFuture.awaitUninterruptibly();
        }

        // Close the connection.  Make sure the close operation ends because
        // all I/O operations are asynchronous in Netty.
        channel.close().awaitUninterruptibly();

        // Shut down all thread pools to exit.
        bootstrap.releaseExternalResources();
    }

    private ChannelFuture processLine(String line, Channel channel) {
        if ( line.startsWith("shrink ") ) {
            URI longURI = readUriFromLine(line, "shrink ");
            if (longURI == null) return null;
            ShurleyShrinkMessage msg = new ShurleyShrinkMessage(VERSION, msgId++, longURI);
            return channel.write(msg);
        } else if (line.startsWith("follow ")) {
            URI longURI = readUriFromLine(line, "follow ");
            if (longURI == null) return null;
            ShurleyFollowMessage msg = new ShurleyFollowMessage(VERSION, msgId++, longURI);
            return channel.write(msg);
        } else if (line.startsWith("list")) {
            System.out.println();
            System.out.println();
            for(Map.Entry<URI,URI> shortening: long2short.entrySet()) {
                System.out.println(shortening.getKey() + " -> "+ shortening.getValue());
            }
            System.out.println();
            return null;
        } else if ( line.equals("bye")){
            return null; // close handled in main loop            
        } else {
            System.out.println(String.format("Unrecognized command '%s'.", line));
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
            System.out.println(String.format("Malformed URL: '%s'.", enteredURI));
            return null;
        }
        return longURI;
    }

    private void printPrompt() {
        System.out.print("shurley> ");
    }

    private void printWelcomeInstructions() {
        System.out.println("Available commands: ");
        System.out.println("  shrink <uri>      -- shrinks the uri provided");
        System.out.println("  follow <uri>      -- follows the uri provided");
        System.out.println("  list              -- list the shortenings so far");
        System.out.println("  bye               -- quit");
        System.out.println();
    }

    public static void main(String[] args) throws Exception {
        // Print usage if no argument is specified.
//        if (args.length != 2) {
//            System.err.println(
//                    "Usage: " + ShurleyClient.class.getSimpleName() +
//                    " <host> <port>");
//            return;
//        }

        // Parse options.
        String host = "localhost";//args[0];
        int port = 8080;//Integer.parseInt(args[1]);

        new ShurleyClient(host, port).run();
    }
}
