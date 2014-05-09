/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurly.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.net.URI;
import java.nio.charset.Charset;

import static io.netty.buffer.Unpooled.buffer;
import static io.netty.buffer.Unpooled.wrappedBuffer;

public class ShurleyMessageEncoder extends MessageToByteEncoder<ShurlyMessage> {
    static final Charset CHARSET = Charset.forName("UTF-8");
    static final int MAGIC_BYTES_AS_INT = 0x5355524C;

    @Override
    protected void encode(ChannelHandlerContext ctx, ShurlyMessage msg, ByteBuf out) throws Exception {

        if (msg != null) {


            final short version = msg.getVersion();
            final long msgId = msg.getMsgId();

            switch (msg.getKind()) {
                case SHRINK: {
                    final ShurlyShrinkMessage shrinkMessage = (ShurlyShrinkMessage) msg;
                    final URI longURI = shrinkMessage.getLongURI();
                    final byte[] encodedLongURI = longURI.toString().getBytes(CHARSET);
                    final ByteBuf header = buffer(4 + 1 + 1 + 4);
                    header.writeInt(MAGIC_BYTES_AS_INT);
                    header.writeByte(version);
                    header.writeByte(shrinkMessage.getKind().getValue());
                    header.writeInt((int) msgId);
                    final ByteBuf body = buffer(4 + encodedLongURI.length);
                    body.writeInt(encodedLongURI.length);
                    body.writeBytes(encodedLongURI);
                    out.writeBytes(wrappedBuffer(header, body));
                    break;
                }

                case SHRUNK: {
                    final ShurlyShrunkMessage shrunkMessage = (ShurlyShrunkMessage) msg;
                    final URI longURI = shrunkMessage.getLongURI();
                    final URI shortURI = shrunkMessage.getShortURI();
                    final byte[] encodedLongURI = longURI.toString().getBytes(CHARSET);
                    final byte[] encodedShortURI = shortURI.toString().getBytes(CHARSET);
                    final ByteBuf header = buffer(4 + 1 + 1 + 4);
                    header.writeInt(MAGIC_BYTES_AS_INT);
                    header.writeByte(version);
                    header.writeByte(shrunkMessage.getKind().getValue());
                    header.writeInt((int) msgId);
                    final ByteBuf body = buffer(4 + encodedLongURI.length + 4 + encodedShortURI.length);
                    body.writeInt(encodedLongURI.length);
                    body.writeBytes(encodedLongURI);
                    body.writeInt(encodedShortURI.length);
                    body.writeBytes(encodedShortURI);
                    out.writeBytes(wrappedBuffer(header, body));
                    break;
                }

                case ERROR: {
                    final ShurlyErrorMessage errorMessage = (ShurlyErrorMessage) msg;
                    final long errorCode = errorMessage.getErrorCode();
                    final byte[] reason = errorMessage.getReason().getBytes(CHARSET);
                    final ByteBuf header = buffer(4 + 1 + 1 + 4);
                    header.writeInt(MAGIC_BYTES_AS_INT);
                    header.writeByte(version);
                    header.writeByte(errorMessage.getKind().getValue());
                    header.writeInt((int) msgId);
                    final ByteBuf body = buffer(4 + 4 + reason.length);
                    body.writeInt((int) errorCode);
                    body.writeInt(reason.length);
                    body.writeBytes(reason);
                    out.writeBytes(wrappedBuffer(header, body));
                    break;
                }

                case FOLLOW: {
                    final ShurlyFollowMessage followMessage = (ShurlyFollowMessage) msg;
                    final URI shortURI = followMessage.getShortURI();
                    final byte[] encodedShortURI = shortURI.toString().getBytes(CHARSET);
                    final ByteBuf header = buffer(4 + 1 + 1 + 4);
                    header.writeInt(MAGIC_BYTES_AS_INT);
                    header.writeByte(version);
                    header.writeByte(followMessage.getKind().getValue());
                    header.writeInt((int) msgId);
                    final ByteBuf body = buffer(4 + encodedShortURI.length);
                    body.writeInt(encodedShortURI.length);
                    body.writeBytes(encodedShortURI);
                    out.writeBytes(wrappedBuffer(header, body));
                    break;
                }

                default:
                    throw new IllegalArgumentException("Unrecognized message kind: " + msg.getKind());
            }
        }
    }
}
