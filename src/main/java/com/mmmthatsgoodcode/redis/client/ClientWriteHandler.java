package com.mmmthatsgoodcode.redis.client;

import com.mmmthatsgoodcode.redis.Command;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.AttributeKey;

public class ClientWriteHandler extends ChannelOutboundHandlerAdapter {

	public final static AttributeKey<Command> COMMAND_ATTRIBUTE = new AttributeKey<Command>("command");
	
	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
		
		if (msg instanceof Command) {
			Command command = (Command) msg;
			ctx.channel().attr(COMMAND_ATTRIBUTE).set(command);
			ctx.write(command.encode());
			
			ctx.flush();
			
		}
		
	}
	
}
