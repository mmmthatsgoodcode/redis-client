package com.mmmthatsgoodcode.redis.client;

import com.mmmthatsgoodcode.redis.Command;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class CommandEncoder extends MessageToByteEncoder<Command> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Command msg, ByteBuf out)
			throws Exception {
		out.writeBytes(msg.encode());
		
	}

}
