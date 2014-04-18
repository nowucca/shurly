/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurley.server;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;

import java.net.URI;
import java.nio.charset.Charset;

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

    short version = 0;
    short command = 0x00;
    int magic;
    long id;

    public ShurleyMessageDecoder() {
        super(DecodeState.READ_MAGIC);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx,
                            Channel channel, ChannelBuffer buf, DecodeState state) throws Exception {

        Object result = null;

        switch (state) {
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
                        int longURILen = buf.readInt();
                        String longURI = buf.readBytes(longURILen).toString(CHARSET);
                        result = new ShurleyShrinkMessage(version, id, URI.create(longURI));
                        break;
                    }
                    case 0x02: {
                        int longURILen = buf.readInt();
                        String longURI = buf.readBytes(longURILen).toString(CHARSET);
                        int shortURILen = buf.readInt();
                        String shortURI = buf.readBytes(shortURILen).toString(CHARSET);
                        result = new ShurleyShrunkMessage(version, id, URI.create(longURI), URI.create(shortURI));
                        break;
                    }
                    case 0x03: {
                        long errorCode = buf.readUnsignedInt();
                        int reasonLength = buf.readInt();
                        String reason = buf.readBytes(reasonLength).toString(CHARSET);
                        result = new ShurleyErrorMessage(version, id, errorCode, reason);
                        break;
                    }
                    case 0x04: {
                        int shortURILen = buf.readInt();
                        String shortURI = buf.readBytes(shortURILen).toString(CHARSET);
                        result = new ShurleyFollowMessage(version, id, URI.create(shortURI));
                        break;
                    }
                    default:
                        throw new IllegalArgumentException("Unrecognized command: " + command);
                }
        }

        assert result != null;
        setState(DecodeState.READ_MAGIC);
        return result;

    }

}
