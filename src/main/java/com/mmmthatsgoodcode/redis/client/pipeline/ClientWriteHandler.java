package com.mmmthatsgoodcode.redis.client.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.redis.Connection;
import com.mmmthatsgoodcode.redis.client.Transaction;
import com.mmmthatsgoodcode.redis.protocol.Command;
import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand;

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
			for (Command command:transaction) {
				ctx.channel().attr(Connection.OUTBOUND).get().add(command);
				ctx.write(command);
			}
			
		} else if (msg instanceof Command) {
			
			AbstractCommand command = (AbstractCommand) msg;
			ctx.channel().attr(Connection.OUTBOUND).get().add(command);
			LOG.debug("Added pending command {} to Channel's outbound queue", command);
			ctx.write(command);
			
		}
		
	}
	
}
