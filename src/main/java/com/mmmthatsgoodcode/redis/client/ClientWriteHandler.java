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
		
		if (msg instanceof Transaction) {
			Transaction transaction = (Transaction) msg;
			for (Request request:transaction) {
				ctx.channel().attr(Connection.OUTBOUND).get().add(request);
				ctx.write(request);
			}
			
		} else if (msg instanceof Request) {
			Request request = (Request) msg;
			ctx.channel().attr(Connection.OUTBOUND).get().add(request);
			LOG.debug("Added pending request {} to Channel's outbound queue", request);
			ctx.write(request);
			
		}
	}
	
}
