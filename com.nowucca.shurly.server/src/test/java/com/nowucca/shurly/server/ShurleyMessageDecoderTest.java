/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurly.server;

import com.nowucca.shurly.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.nio.charset.Charset;

import static io.netty.buffer.Unpooled.directBuffer;
import static org.junit.Assert.assertEquals;

public class ShurleyMessageDecoderTest {

    ShurleyMessageDecoder decoder;
    EmbeddedChannel channel;


    @Before
    public void setUp() throws Exception {
        channel = new EmbeddedChannel(decoder = new ShurleyMessageDecoder());
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void shouldDecodeValidShrinkMessage() throws Exception {
        final String uriString = "http://example.com/shortenmeplease";
        byte[] longURI = uriString.getBytes(Charset.forName("UTF-8"));

        ByteBuf buf = directBuffer(1000);
        buf.writeInt(ShurleyMessageDecoder.MAGIC_BYTES_AS_INT);
        buf.writeByte((byte) 0x01);        // version
        buf.writeByte((byte)0x01);         // command - shrink
        buf.writeInt(1);               // message id
        buf.writeInt(longURI.length);  // url length
        buf.writeBytes(longURI);            // url
        channel.writeInbound(buf);

        ShurlyMessage message = (ShurlyMessage) channel.readInbound();
        assertEquals(ShurlyMessage.Kind.SHRINK, message.getKind());
        ShurlyShrinkMessage shrinkMessage = (ShurlyShrinkMessage) message;
        assertEquals(1, message.getMsgId());
        assertEquals(1, message.getVersion());
        assertEquals(URI.create(uriString), shrinkMessage.getLongURI());
        channel.finish();
    }

    @Test
    public void shouldDecodeValidShrunkMessage() throws Exception {
        final String longUriString = "http://example.com/shortenmeplease";
        final String shortUriString = "http://shure.ly/smp";
        byte[][] uris = Utils.stringsToByteArrays(longUriString, shortUriString);

        ByteBuf buf = directBuffer(1000);
        buf.writeInt(ShurleyMessageDecoder.MAGIC_BYTES_AS_INT);
        buf.writeByte((byte) 0x01);        // version
        buf.writeByte((byte) 0x02);         // command - shrunk
        buf.writeInt(2);               // message id
        buf.writeInt(uris[0].length);  // url length
        buf.writeBytes(uris[0]);            // url
        buf.writeInt(uris[1].length);  // url length
        buf.writeBytes(uris[1]);            // url
        channel.writeInbound(buf);

        ShurlyMessage message = (ShurlyMessage) channel.readInbound();
        assertEquals(ShurlyMessage.Kind.SHRUNK, message.getKind());
        ShurlyShrunkMessage shrinkMessage = (ShurlyShrunkMessage) message;
        assertEquals(2, message.getMsgId());
        assertEquals(1, message.getVersion());
        assertEquals(URI.create(longUriString), shrinkMessage.getLongURI());
        assertEquals(URI.create(shortUriString), shrinkMessage.getShortURI());
        channel.finish();
    }

    @Test
    public void shouldDecodeValidErrorMessage() throws Exception {
        final String reason = "A unit test sample reason";
        byte[][] data = Utils.stringsToByteArrays(reason);

        ByteBuf buf = directBuffer(1000);
        buf.writeInt(ShurleyMessageDecoder.MAGIC_BYTES_AS_INT);
        buf.writeByte((byte) 0x01);        // version
        buf.writeByte((byte) 0x03);         // command - error
        buf.writeInt(2);               // message id
        buf.writeInt(4514);                // error code
        buf.writeInt(data[0].length);  // reason length
        buf.writeBytes(data[0]);            // reason
        channel.writeInbound(buf);

        ShurlyMessage message = (ShurlyMessage) channel.readInbound();
        assertEquals(ShurlyMessage.Kind.ERROR, message.getKind());
        ShurlyErrorMessage msg = (ShurlyErrorMessage) message;
        assertEquals(2, message.getMsgId());
        assertEquals(1, message.getVersion());
        assertEquals(4514, msg.getErrorCode());
        assertEquals(reason, msg.getReason());
        channel.finish();
    }



}
