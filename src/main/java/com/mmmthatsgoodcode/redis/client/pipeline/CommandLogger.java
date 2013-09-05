package com.mmmthatsgoodcode.redis.client.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class CommandLogger extends ChannelOutboundHandlerAdapter {

	private final static Logger LOG = LoggerFactory.getLogger(CommandLogger.class);
	
	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
		
		
		if (LOG.isDebugEnabled() && msg instanceof ByteBuf) {
			ByteBuf out = (ByteBuf) msg;
			LOG.debug("Outbound UTF8 encoded bytes\n:{}:", new String(UnpooledByteBufAllocator.DEFAULT.heapBuffer().writeBytes(out, 0, out.readableBytes()).array()));
			
		}
		
		ctx.writeAndFlush(msg);
		
	}
	
}
