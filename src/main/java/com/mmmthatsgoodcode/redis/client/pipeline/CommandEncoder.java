package com.mmmthatsgoodcode.redis.client.pipeline;

import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.redis.Client;
import com.mmmthatsgoodcode.redis.Connection;
import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand;
import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand.EncodeHelper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.buffer.UnpooledHeapByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class CommandEncoder extends MessageToByteEncoder<AbstractCommand> {

	private static final Logger LOG = LoggerFactory.getLogger(CommandEncoder.class);
	private final Client client;
	
	public CommandEncoder(Client client) {
		this.client = client;
	}
	
	@Override
	protected void encode(ChannelHandlerContext ctx, AbstractCommand msg, ByteBuf out)
			throws Exception {
		
		LOG.debug("Encoding outbound command {}", msg);
		
		EncodeHelper helper = new AbstractCommand.EncodeHelper(out);
		ByteBuf buf = msg.encode();
		if (msg.getArgc() > 0) helper.addArgc(msg.getArgc());
		LOG.debug("Encoded command {}", msg);
		helper.buffer().writeBytes(buf);
		buf.release();
		
//		ctx.flush();
		
		LOG.debug("Sent command {}", msg);
		
	}
	
    @Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	
    	Connection connection = ctx.channel().attr(Connection.CONNECTION).get();
    	if (connection != null) connection.discard(cause);
    	
    }

}
