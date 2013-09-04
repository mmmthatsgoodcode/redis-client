package com.mmmthatsgoodcode.redis.client.pipeline;

import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.redis.Client;
import com.mmmthatsgoodcode.redis.Connection;
import com.mmmthatsgoodcode.redis.Protocol;
import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.buffer.UnpooledHeapByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class CommandEncoder extends MessageToByteEncoder<AbstractCommand> {

	private static final Logger LOG = LoggerFactory.getLogger(CommandEncoder.class);
	private final Protocol protocol;
	
	public CommandEncoder(Protocol protocol) {
		this.protocol = protocol;
	}
	
	@Override
	protected void encode(ChannelHandlerContext ctx, AbstractCommand msg, ByteBuf out)
			throws Exception {
		
		LOG.debug("Encoding outbound command {}", msg);
		
		protocol.getEncoder().encode(msg, out);
		LOG.debug("Encoded command {}", msg);
		
	}
	
    @Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	
    	Connection connection = ctx.channel().attr(Connection.CONNECTION).get();
    	if (connection != null) connection.discard(cause);
    	
    }

}
