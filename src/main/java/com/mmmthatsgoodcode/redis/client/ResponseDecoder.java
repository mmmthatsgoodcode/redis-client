package com.mmmthatsgoodcode.redis.client;

import java.util.Arrays;
import java.util.List;

import com.mmmthatsgoodcode.redis.Response;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class ResponseDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		System.out.println("Incoming? - "+ctx.channel().attr(ClientWriteHandler.COMMAND_ATTRIBUTE));
		
		// see if the buffer ends in RESPONSE_END
		if (in.readableBytes() > Response.Delimiters.RESPONSE_END.length) {
			byte[] end = new byte[Response.Delimiters.RESPONSE_END.length];
			
			in.getBytes((in.readableBytes()-end.length)-1, end);
			if (Arrays.equals(end, Response.Delimiters.RESPONSE_END)) {
				// lets start parsing
				String response = new String(in.readBytes(in.readableBytes()).array());
				System.out.println(response);
				
				
			}
		}
		
		return;
		
	}

}
