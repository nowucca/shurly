/**
 * Copyright (c) 2012-2014, Steven Atkinson. All rights reserved.
 */
package com.nowucca.shurly.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;

public class ShurleyMessageDecoder extends ReplayingDecoder<ShurleyMessageDecoder.DecodeState> {

    static final Charset CHARSET = Charset.forName("UTF-8");
    static final int MAGIC_BYTES_AS_INT = 0x5355524C;

    public static enum DecodeState {
        READ_MAGIC,
        READ_VERSION,
        READ_COMMAND,
        READ_MSGID,
        READ_COMMAND_DATA
    }

    short version;
    short command;
    int magic;
    long id;

    public ShurleyMessageDecoder() {
        super(DecodeState.READ_MAGIC);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {


        Object result = null;

        switch (state()) {
            case READ_MAGIC:
                magic = buf.readInt();
                if (magic == MAGIC_BYTES_AS_INT) {
                    checkpoint(DecodeState.READ_VERSION);
                } else {
                    throw new IllegalArgumentException(
                            String.format("Expected to read magic 4 bytes: %d but received %d."
                                    , 0x5355524C, magic));
                }
            case READ_VERSION:
                version = buf.readUnsignedByte();
                checkpoint(DecodeState.READ_COMMAND);

            case READ_COMMAND:
                command = buf.readUnsignedByte();
                checkpoint(DecodeState.READ_MSGID);

            case READ_MSGID:
                id = buf.readUnsignedInt();
                checkpoint(DecodeState.READ_COMMAND_DATA);

            case READ_COMMAND_DATA:
                switch (command) {
                    case 0x01: {
                        final int longURILen = buf.readInt();
                        final String longURI = buf.readBytes(longURILen).toString(CHARSET);
                        result = new ShurlyShrinkMessage(version, id, URI.create(longURI));
                        break;
                    }
                    case 0x02: {
                        final int longURILen = buf.readInt();
                        final String longURI = buf.readBytes(longURILen).toString(CHARSET);
                        final int shortURILen = buf.readInt();
                        final String shortURI = buf.readBytes(shortURILen).toString(CHARSET);
                        result = new ShurlyShrunkMessage(version, id, URI.create(longURI), URI.create(shortURI));
                        break;
                    }
                    case 0x03: {
                        final long errorCode = buf.readUnsignedInt();
                        final int reasonLength = buf.readInt();
                        final String reason = buf.readBytes(reasonLength).toString(CHARSET);
                        result = new ShurlyErrorMessage(version, id, errorCode, reason);
                        break;
                    }
                    case 0x04: {
                        final int shortURILen = buf.readInt();
                        final String shortURI = buf.readBytes(shortURILen).toString(CHARSET);
                        result = new ShurlyFollowMessage(version, id, URI.create(shortURI));
                        break;
                    }
                    default:
                        throw new IllegalArgumentException("Unrecognized command: " + command);
                }
        }

        //noinspection ConstantConditions
        assert result != null;
        state(DecodeState.READ_MAGIC);
        out.add(result);
    }

}
