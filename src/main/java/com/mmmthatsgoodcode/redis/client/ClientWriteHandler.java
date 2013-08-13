package com.mmmthatsgoodcode.redis.client;

import com.mmmthatsgoodcode.redis.protocol.Request;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.AttributeKey;

public class ClientWriteHandler extends ChannelOutboundHandlerAdapter {

	public final static AttributeKey<Request> REQUEST_ATTRIBUTE = new AttributeKey<Request>("request");
	
	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
		
		if (msg instanceof Request) {
			Request request = (Request) msg;
			ctx.channel().attr(REQUEST_ATTRIBUTE).set(request);
			ctx.write(request);
			
			ctx.flush();
			
		}
		
	}
	
}
