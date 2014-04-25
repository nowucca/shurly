/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurley.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.net.URI;
import java.nio.charset.Charset;

import static io.netty.buffer.Unpooled.buffer;
import static io.netty.buffer.Unpooled.wrappedBuffer;

public class ShurleyMessageEncoder extends MessageToByteEncoder<ShurleyMessage> {
    static final Charset CHARSET = Charset.forName("UTF-8");
    static final int MAGIC_BYTES_AS_INT = 0x5355524C;

    @Override
    protected void encode(ChannelHandlerContext ctx, ShurleyMessage msg, ByteBuf out) throws Exception {

        if (msg != null) {


            short version = msg.getVersion();
            long msgId = msg.getMsgId();

            switch ( msg.getKind() ) {
                case SHRINK: {
                    ShurleyShrinkMessage shrinkMessage = (ShurleyShrinkMessage) msg;
                    URI longURI = shrinkMessage.getLongURI();
                    byte[] encodedLongURI = longURI.toString().getBytes(CHARSET);
                    ByteBuf header = buffer(4 + 1 + 1 + 4);
                    header.writeInt(MAGIC_BYTES_AS_INT);
                    header.writeByte(version);
                    header.writeByte(shrinkMessage.getKind().getValue());
                    header.writeInt((int)msgId);
                    ByteBuf body = buffer(4 + encodedLongURI.length);
                    body.writeInt(encodedLongURI.length);
                    body.writeBytes(encodedLongURI);
                    out.writeBytes(wrappedBuffer(header, body));
                }
                break;
                
                case SHRUNK: {
                    ShurleyShrunkMessage shrunkMessage = (ShurleyShrunkMessage) msg;
                    URI longURI = shrunkMessage.getLongURI();
                    URI shortURI = shrunkMessage.getShortURI();
                    byte[] encodedLongURI = longURI.toString().getBytes(CHARSET);
                    byte[] encodedShortURI = shortURI.toString().getBytes(CHARSET);
                    ByteBuf header = buffer(4 + 1 + 1 + 4);
                    header.writeInt(MAGIC_BYTES_AS_INT);
                    header.writeByte(version);
                    header.writeByte(shrunkMessage.getKind().getValue());
                    header.writeInt((int)msgId);
                    ByteBuf body = buffer(4 + encodedLongURI.length + 4 + encodedShortURI.length);
                    body.writeInt(encodedLongURI.length);
                    body.writeBytes(encodedLongURI);
                    body.writeInt(encodedShortURI.length);
                    body.writeBytes(encodedShortURI);
                    out.writeBytes(wrappedBuffer(header, body));
                }
                break;

                case ERROR: {
                    ShurleyErrorMessage errorMessage = (ShurleyErrorMessage) msg;
                    long errorCode = errorMessage.getErrorCode();
                    byte[] reason = errorMessage.getReason().getBytes(CHARSET);
                    ByteBuf header = buffer(4 + 1 + 1 + 4);
                    header.writeInt(MAGIC_BYTES_AS_INT);
                    header.writeByte(version);
                    header.writeByte(errorMessage.getKind().getValue());
                    header.writeInt((int)msgId);
                    ByteBuf body = buffer(4 + 4 + reason.length);
                    body.writeInt((int)errorCode);
                    body.writeInt(reason.length);
                    body.writeBytes(reason);
                    out.writeBytes(wrappedBuffer(header, body));
                }
                break;

                case FOLLOW: {
                    ShurleyFollowMessage followMessage = (ShurleyFollowMessage) msg;
                    URI shortURI = followMessage.getShortURI();
                    byte[] encodedShortURI = shortURI.toString().getBytes(CHARSET);
                    ByteBuf header = buffer(4 + 1 + 1 + 4);
                    header.writeInt(MAGIC_BYTES_AS_INT);
                    header.writeByte(version);
                    header.writeByte(followMessage.getKind().getValue());
                    header.writeInt((int)msgId);
                    ByteBuf body = buffer(4 + encodedShortURI.length);
                    body.writeInt(encodedShortURI.length);
                    body.writeBytes(encodedShortURI);
                    out.writeBytes(wrappedBuffer(header, body));
                }
                break;

                default:
                    throw new IllegalArgumentException("Unrecognized message kind: " + msg.getKind());


            }
        }
    }
}
