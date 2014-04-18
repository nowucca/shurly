/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurley.server;

import com.nowucca.shurley.Utils;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ShurleyMessageDecoderTest {

    ShurleyMessageDecoder decoder;
    DecoderEmbedder<ShurleyMessage> e;


    @Before
    public void setUp() throws Exception {
        e = new DecoderEmbedder<ShurleyMessage>(decoder = new ShurleyMessageDecoder());
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void shouldDecodeValidShrinkMessage() throws Exception {
        final String uriString = "http://example.com/shortenmeplease";
        byte[] longURI = uriString.getBytes(Charset.forName("UTF-8"));

        ByteBuffer buf = ByteBuffer.allocateDirect(1000);
        buf.putInt(ShurleyMessageDecoder.MAGIC_BYTES_AS_INT);
        buf.put((byte) 0x01);        // version
        buf.put((byte)0x01);         // command - shrink
        buf.putInt(1);               // message id
        buf.putInt(longURI.length);  // url length
        buf.put(longURI);            // url
        buf.flip();
        e.offer(ChannelBuffers.wrappedBuffer(buf));

        ShurleyMessage message = e.poll();
        assertEquals(ShurleyMessage.Kind.SHRINK, message.getKind());
        ShurleyShrinkMessage shrinkMessage = (ShurleyShrinkMessage) message;
        assertEquals(1, message.getMsgId());
        assertEquals(1, message.getVersion());
        assertEquals(URI.create(uriString), shrinkMessage.getLongURI());
        e.finish();
    }




    @Test
    public void shouldDecodeValidShrunkMessage() throws Exception {
        final String longUriString = "http://example.com/shortenmeplease";
        final String shortUriString = "http://shure.ly/smp";
        byte[][] uris = Utils.stringsToByteArrays(longUriString, shortUriString);

        ByteBuffer buf = ByteBuffer.allocateDirect(1000);
        buf.putInt(ShurleyMessageDecoder.MAGIC_BYTES_AS_INT);
        buf.put((byte) 0x01);        // version
        buf.put((byte) 0x02);         // command - shrunk
        buf.putInt(2);               // message id
        buf.putInt(uris[0].length);  // url length
        buf.put(uris[0]);            // url
        buf.putInt(uris[1].length);  // url length
        buf.put(uris[1]);            // url
        buf.flip();
        e.offer(ChannelBuffers.wrappedBuffer(buf));

        ShurleyMessage message = e.poll();
        assertEquals(ShurleyMessage.Kind.SHRUNK, message.getKind());
        ShurleyShrunkMessage shrinkMessage = (ShurleyShrunkMessage) message;
        assertEquals(2, message.getMsgId());
        assertEquals(1, message.getVersion());
        assertEquals(URI.create(longUriString), shrinkMessage.getLongURI());
        assertEquals(URI.create(shortUriString), shrinkMessage.getShortURI());
        e.finish();
    }

    @Test
    public void shouldDecodeValidErrorMessage() throws Exception {
        final String reason = "A unit test sample reason";
        byte[][] data = Utils.stringsToByteArrays(reason);

        ByteBuffer buf = ByteBuffer.allocateDirect(1000);
        buf.putInt(ShurleyMessageDecoder.MAGIC_BYTES_AS_INT);
        buf.put((byte) 0x01);        // version
        buf.put((byte) 0x03);         // command - error
        buf.putInt(2);               // message id
        buf.putInt(4514);                // error code
        buf.putInt(data[0].length);  // reason length
        buf.put(data[0]);            // reason
        buf.flip();
        e.offer(ChannelBuffers.wrappedBuffer(buf));

        ShurleyMessage message = e.poll();
        assertEquals(ShurleyMessage.Kind.ERROR, message.getKind());
        ShurleyErrorMessage msg = (ShurleyErrorMessage) message;
        assertEquals(2, message.getMsgId());
        assertEquals(1, message.getVersion());
        assertEquals(4514, msg.getErrorCode());
        assertEquals(reason, msg.getReason());
        e.finish();
    }


    
}
