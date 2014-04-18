/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurley.server;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import java.net.URI;
import java.nio.charset.Charset;

public class ShurleyMessageEncoder extends OneToOneEncoder {
    static final Charset CHARSET = Charset.forName("UTF-8");
    static final int MAGIC_BYTES_AS_INT = 0x5355524C;

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {

        if ( msg instanceof ShurleyMessage ) {

            ShurleyMessage shurleyMessage = (ShurleyMessage) msg;

            short version = shurleyMessage.getVersion();
            long msgId = shurleyMessage.getMsgId();

            switch ( shurleyMessage.getKind() ) {
                case SHRINK: {
                    ShurleyShrinkMessage shrinkMessage = (ShurleyShrinkMessage) msg;
                    URI longURI = shrinkMessage.getLongURI();
                    byte[] encodedLongURI = longURI.toString().getBytes(CHARSET);
                    ChannelBuffer header = ChannelBuffers.buffer(4 + 1 + 1 + 4);
                    header.writeInt(MAGIC_BYTES_AS_INT);
                    header.writeByte(version);
                    header.writeByte(shrinkMessage.getKind().getValue());
                    header.writeInt((int)msgId);
                    ChannelBuffer body = ChannelBuffers.buffer(4 + encodedLongURI.length);
                    body.writeInt(encodedLongURI.length);
                    body.writeBytes(encodedLongURI);
                    return ChannelBuffers.wrappedBuffer(header, body);
                }
                
                case SHRUNK: {
                    ShurleyShrunkMessage shrunkMessage = (ShurleyShrunkMessage) msg;
                    URI longURI = shrunkMessage.getLongURI();
                    URI shortURI = shrunkMessage.getShortURI();
                    byte[] encodedLongURI = longURI.toString().getBytes(CHARSET);
                    byte[] encodedShortURI = shortURI.toString().getBytes(CHARSET);
                    ChannelBuffer header = ChannelBuffers.buffer(4 + 1 + 1 + 4);
                    header.writeInt(MAGIC_BYTES_AS_INT);
                    header.writeByte(version);
                    header.writeByte(shrunkMessage.getKind().getValue());
                    header.writeInt((int)msgId);
                    ChannelBuffer body = ChannelBuffers.buffer(4 + encodedLongURI.length + 4 + encodedShortURI.length);
                    body.writeInt(encodedLongURI.length);
                    body.writeBytes(encodedLongURI);
                    body.writeInt(encodedShortURI.length);
                    body.writeBytes(encodedShortURI);
                    return ChannelBuffers.wrappedBuffer(header,body);
                }

                case ERROR: {
                    ShurleyErrorMessage errorMessage = (ShurleyErrorMessage) msg;
                    long errorCode = errorMessage.getErrorCode();
                    byte[] reason = errorMessage.getReason().getBytes(CHARSET);
                    ChannelBuffer header = ChannelBuffers.buffer(4 + 1 + 1 + 4);
                    header.writeInt(MAGIC_BYTES_AS_INT);
                    header.writeByte(version);
                    header.writeByte(errorMessage.getKind().getValue());
                    header.writeInt((int)msgId);
                    ChannelBuffer body = ChannelBuffers.buffer(4 + 4 + reason.length);
                    body.writeInt((int)errorCode);
                    body.writeInt(reason.length);
                    body.writeBytes(reason);
                    return ChannelBuffers.wrappedBuffer(header,body);
                }
                case FOLLOW: {
                    ShurleyFollowMessage followMessage = (ShurleyFollowMessage) msg;
                    URI shortURI = followMessage.getShortURI();
                    byte[] encodedShortURI = shortURI.toString().getBytes(CHARSET);
                    ChannelBuffer header = ChannelBuffers.buffer(4 + 1 + 1 + 4);
                    header.writeInt(MAGIC_BYTES_AS_INT);
                    header.writeByte(version);
                    header.writeByte(followMessage.getKind().getValue());
                    header.writeInt((int)msgId);
                    ChannelBuffer body = ChannelBuffers.buffer(4 + encodedShortURI.length);
                    body.writeInt(encodedShortURI.length);
                    body.writeBytes(encodedShortURI);
                    return ChannelBuffers.wrappedBuffer(header,body);
                }

            }
        }
        return msg;
    }
}
