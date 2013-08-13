package com.mmmthatsgoodcode.redis.client;

import com.mmmthatsgoodcode.redis.protocol.Request;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RequestEncoder extends MessageToByteEncoder<Request> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Request msg, ByteBuf out)
			throws Exception {
		
		out.writeBytes(msg.encode());
	}

}
