package com.mmmthatsgoodcode.redis.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.redis.Connection;
import com.mmmthatsgoodcode.redis.protocol.Request;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.AttributeKey;

public class ClientWriteHandler extends ChannelOutboundHandlerAdapter {
	
	private final static Logger LOG = LoggerFactory.getLogger(ClientWriteHandler.class);
	
	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
		
		if (msg instanceof Request) {
			Request request = (Request) msg;
			ctx.channel().attr(Connection.OUTBOUND).get().add(request);
			LOG.debug("Saved outbound request");
			ctx.write(request);
			
			ctx.flush();
			
		}
		
	}
	
}
