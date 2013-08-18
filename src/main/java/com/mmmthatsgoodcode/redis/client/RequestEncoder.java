package com.mmmthatsgoodcode.redis.client;

import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.redis.protocol.Request;
import com.mmmthatsgoodcode.redis.protocol.Request.EncodeHelper;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.buffer.UnpooledHeapByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RequestEncoder extends MessageToByteEncoder<Request> {

	private static final Logger LOG = LoggerFactory.getLogger(RequestEncoder.class);
	
	@Override
	protected void encode(ChannelHandlerContext ctx, Request msg, ByteBuf out)
			throws Exception {
		
		LOG.debug("Outbound request {}", msg);
		
		EncodeHelper helper = new Request.EncodeHelper(out);
		ByteBuf buf = msg.encode();
		if (msg.getArgc() > 0) helper.addArgc(msg.getArgc());
		helper.buffer().writeBytes(buf);
		buf.release();
		LOG.debug("Sent request {}, {}", msg, msg.getName()+" "+msg.getArgc());
		
	}

}
