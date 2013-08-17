package com.mmmthatsgoodcode.redis.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.redis.protocol.Request;
import com.mmmthatsgoodcode.redis.protocol.Response;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.AttributeKey;

public class ResponseDecoder extends ByteToMessageDecoder {

	private final static Logger LOG = LoggerFactory.getLogger(ResponseDecoder.class);

	private List<Response> responses = new ArrayList<Response>();
	private Response currentResponse = null;
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		
		// if there are readable bytes on the buffer - there should be..
		while (in.readableBytes() > 1) {
			LOG.debug("Reading from index {}", in.readerIndex());
			// first, find out what kind of response this is ( if the first byte is already available on the buffer - it should be )
			if (currentResponse == null) {
				currentResponse = Response.infer(in);
				LOG.debug("Inferred next response in buffer to be: {}", currentResponse);
			}

			// decode contents from the buffer
			if (currentResponse != null && currentResponse.decode() == true) {
				// decoder finished
				responses.add(currentResponse);
				LOG.debug("Decoded response: {}", currentResponse.value());
				currentResponse = null;

				LOG.debug("BUffer after decode: {}/{}", in.readerIndex(), in.readableBytes());
				// see if we are done completely decoding the buffer
				if (in.readableBytes() == 2 && in.forEachByte(ByteBufProcessor.FIND_CRLF) != -1) {
					// last two bytes is a CRLF
					LOG.debug("End of buffer");
					in.clear();
					
					ctx.fireChannelRead(responses);
					return;

				}
				
				// there are still bytes in the buffer after decode, that are not CRLF
				LOG.debug("Still {} bytes buffered to go..", in.readableBytes());
				
				// skip the delimiter
				if (in.readableBytes() > 2) in.readerIndex(in.readerIndex()+2);
				else in.readerIndex(in.writerIndex());
				

			} else {

				// bytes in buffer were not enough to decode a response, continue..
				return;
			
			}

		
		}
		
		return;
		
	}

}
