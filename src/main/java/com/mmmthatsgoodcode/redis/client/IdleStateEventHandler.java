package com.mmmthatsgoodcode.redis.client;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.redis.protocol.request.Ping;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class IdleStateEventHandler extends ChannelDuplexHandler {

	private final static Logger LOG = LoggerFactory.getLogger(IdleStateEventHandler.class);

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
	
		if (evt instanceof IdleState) {
			LOG.debug("Received IdleState event, sending PING");
			ctx.write(new Ping());
		}
		
	}

}
