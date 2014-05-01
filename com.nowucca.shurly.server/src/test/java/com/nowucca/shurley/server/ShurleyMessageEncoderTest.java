/**
 * Copyright (c) 2012-2014 Steven Atkinson.  All rights reserved
 */
package com.nowucca.shurley.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.nio.charset.Charset;

import static io.netty.buffer.Unpooled.copiedBuffer;
import static java.lang.String.format;

public class ShurleyMessageEncoderTest {

    ShurleyMessageEncoder encoder;
    EmbeddedChannel channel;

    private static final Charset CHARSET = Charset.forName("UTF-8");

    @Before
    public void setUp() throws Exception {
        channel = new EmbeddedChannel(encoder = new ShurleyMessageEncoder());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void shouldEncodeValidShrinkMessage() throws Exception {
        final String uriString = "http://example.com/shortenmeplease";
        final URI uri = URI.create(uriString);
        ShurleyShrinkMessage shrinkMsg = new ShurleyShrinkMessage((short) 0x01, 1L, uri);
        channel.writeOutbound(shrinkMsg);

        ByteBuf buffer = (ByteBuf) channel.readOutbound();

        byte[] magic = new byte[]{0x53, 0x55, 0x52, 0x4c}; // magic
        byte[] version = new byte[]{0x01}; // version
        byte[] command = new byte[]{0x01}; // command
        byte[] msgId = new byte[]{0x00, 0x00, 0x00, 0x01}; // msgId
        byte[] len = new byte[]{0x00, 0x00, 0x00, 0x22}; // uri length
        byte[] uriBytes = uriString.getBytes(CHARSET); // uri UTF-8 encoded

        ByteBuf expected = copiedBuffer(magic, version, command, msgId, len, uriBytes);

        Assert.assertTrue(ByteBufUtil.equals(expected, buffer));

        channel.finish();
    }


    @Test
    public void shouldEncodeValidShrunkMessage() throws Exception {
        final String uriString = "http://example.com/shortenmeplease";
        final URI uri = URI.create(uriString);

        final String shortUriString = "http://shure.ly/aabb";
        final URI shortURI = URI.create(shortUriString);
        ShurleyShrunkMessage msg = new ShurleyShrunkMessage((short) 0x01, 1L, uri, shortURI);
        channel.writeOutbound(msg);

        ByteBuf buffer = (ByteBuf) channel.readOutbound();

        byte[] magic = new byte[]{0x53, 0x55, 0x52, 0x4c}; // magic
        byte[] version = new byte[]{0x01}; // version
        byte[] command = new byte[]{0x02}; // command
        byte[] msgId = new byte[]{0x00, 0x00, 0x00, 0x01}; // msgId
        byte[] len = new byte[]{0x00, 0x00, 0x00, 0x22}; // uri length
        byte[] uriBytes = uriString.getBytes(CHARSET); // uri UTF-8 encoded
        byte[] shortLen = new byte[]{0x00, 0x00, 0x00, 0x14}; // short uri length
        byte[] shortUriBytes = shortUriString.getBytes(CHARSET);
        ByteBuf expected = copiedBuffer(magic, version, command, msgId,
                len, uriBytes, shortLen, shortUriBytes);

        Assert.assertTrue(format("Expected bytes '%s'\nreceived:\n               '%s'",
                        ByteBufUtil.hexDump(expected), ByteBufUtil.hexDump(buffer)),
                        ByteBufUtil.equals(expected, buffer));

        channel.finish();
    }

    @Test
    public void shouldEncodeValidFollowMessage() throws Exception {

        final String shortUriString = "http://shure.ly/aabb";
        final URI shortURI = URI.create(shortUriString);
        ShurleyFollowMessage msg = new ShurleyFollowMessage((short) 0x01, 1L, shortURI);
        channel.writeOutbound(msg);

        ByteBuf buffer = (ByteBuf) channel.readOutbound();

        byte[] magic = new byte[]{0x53, 0x55, 0x52, 0x4c}; // magic
        byte[] version = new byte[]{0x01}; // version
        byte[] command = new byte[]{0x04}; // command
        byte[] msgId = new byte[]{0x00, 0x00, 0x00, 0x01}; // msgId
        byte[] shortLen = new byte[]{0x00, 0x00, 0x00, 0x14}; // short uri length
        byte[] shortUriBytes = shortUriString.getBytes(CHARSET);
        ByteBuf expected = copiedBuffer(magic, version, command, msgId,
                shortLen, shortUriBytes);

        Assert.assertTrue(format("Expected bytes '%s'\nreceived:\n               '%s'",
                        ByteBufUtil.hexDump(expected), ByteBufUtil.hexDump(buffer)),
                        ByteBufUtil.equals(expected, buffer));

        channel.finish();
    }


    @Test
    public void shouldEncodeValidErrorMessage() throws Exception {

        final String reason = "Sample error";
        final long errorCodeIn = 1L;
        ShurleyErrorMessage msg = new ShurleyErrorMessage((short) 1, 1L, errorCodeIn, reason);
        channel.writeOutbound(msg);

        ByteBuf buffer = (ByteBuf) channel.readOutbound();

        byte[] magic = new byte[]{0x53, 0x55, 0x52, 0x4c}; // magic
        byte[] version = new byte[]{0x01}; // version
        byte[] command = new byte[]{0x03}; // command
        byte[] msgId = new byte[]{0x00, 0x00, 0x00, 0x01}; // msgId
        byte[] errorCode = new byte[]{0x00, 0x00, 0x00, 0x01}; // msgId
        byte[] reasonLen = new byte[]{0x00, 0x00, 0x00, 0x0c}; // reason length
        byte[] reasonBytes = reason.getBytes(CHARSET); // uri UTF-8 encoded
        ByteBuf expected = copiedBuffer(magic, version, command, msgId,
                errorCode, reasonLen, reasonBytes);


        Assert.assertTrue(format("Expected bytes '%s'\nreceived:\n               '%s'",
                ByteBufUtil.hexDump(expected), ByteBufUtil.hexDump(buffer)),
                ByteBufUtil.equals(expected, buffer));

        channel.finish();
    }
}
