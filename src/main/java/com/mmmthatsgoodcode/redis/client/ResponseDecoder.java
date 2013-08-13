package com.mmmthatsgoodcode.redis.client;

import java.util.Arrays;
import java.util.List;

import com.mmmthatsgoodcode.redis.protocol.Request;
import com.mmmthatsgoodcode.redis.protocol.Response;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.AttributeKey;

public class ResponseDecoder extends ByteToMessageDecoder {

	public final static AttributeKey<Response> RESPONSE_ATTRIBUTE = new AttributeKey<Response>("response");

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		
		// if the first byte is already available on the buffer - it should be..
		if (in.readableBytes() >= 1) {
			// first, find out what kind of request this is ( if the first byte is already available on the buffer - it should be )
			if (ctx.channel().attr(RESPONSE_ATTRIBUTE).get() == null) ctx.channel().attr(RESPONSE_ATTRIBUTE).set(Response.infer(in));
			Response response = ctx.channel().attr(RESPONSE_ATTRIBUTE).get();
			// see if we are done decoding the buffer
			if (response.decode() == true) {
				in.clear();

				ctx.channel().attr(RESPONSE_ATTRIBUTE).remove();
				Request request = ctx.channel().attr(ClientWriteHandler.REQUEST_ATTRIBUTE).getAndRemove();
				request.getResponse().finalize(response);

				out.add(response);	

			}
		
		}
		
		return;
		
	}

}
