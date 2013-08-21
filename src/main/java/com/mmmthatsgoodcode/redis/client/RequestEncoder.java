package com.mmmthatsgoodcode.redis.client;

import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.redis.Connection;
import com.mmmthatsgoodcode.redis.protocol.AbstractRequest;
import com.mmmthatsgoodcode.redis.protocol.AbstractRequest.EncodeHelper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.buffer.UnpooledHeapByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RequestEncoder extends MessageToByteEncoder<AbstractRequest> {

	private static final Logger LOG = LoggerFactory.getLogger(RequestEncoder.class);
	
	@Override
	protected void encode(ChannelHandlerContext ctx, AbstractRequest msg, ByteBuf out)
			throws Exception {
		
		LOG.debug("Encoding outbound request {}", msg);
		
		EncodeHelper helper = new AbstractRequest.EncodeHelper(out);
		ByteBuf buf = msg.encode();
		if (msg.getArgc() > 0) helper.addArgc(msg.getArgc());
		LOG.debug("Encoded request {}", msg);
		helper.buffer().writeBytes(buf);
		buf.release();
		
//		ctx.flush();
		
		LOG.debug("Sent request {}", msg);
		
	}
	
    @Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	
    	LOG.warn("?!");
    	Connection connection = ctx.channel().attr(Connection.CONNECTION).get();
    	if (connection != null) connection.discard(cause);
    	
    }

}
