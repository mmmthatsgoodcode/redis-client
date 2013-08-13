package com.mmmthatsgoodcode.redis.command;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;
import io.netty.buffer.PooledByteBufAllocator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.primitives.Bytes;
import com.mmmthatsgoodcode.redis.Command;
import com.mmmthatsgoodcode.redis.Response;
import com.mmmthatsgoodcode.redis.Command.Decoder;
import com.mmmthatsgoodcode.redis.Util;

public class Get extends Command {

	public static final byte[] RESULT_LENGTH_BEGIN = "$".getBytes();
	public static final byte[] RESULT_LENGTH_END = "\r\n".getBytes();
	
	private static final String COMMAND = "GET";
	
	
	public Get(String key) {
		addParamter(key);
	}
	
	@Override
	public String getName() {
		return COMMAND;
	}

	@Override
	public Decoder getDecoder() {
		
		return new Decoder() {
						
			@Override
			public List<Response> decode(ByteBuf buf) {
				
				List<Response> responses = new ArrayList<Response>();

				int s = buf.forEachByte(new Util.ByteArrayInBuf(RESULT_LENGTH_BEGIN));
				if (s > -1) {
					buf.readerIndex(s+1); // move to after the marker
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					
					while(buf.isReadable()) {
						byte nextByte = buf.getByte(buf.readerIndex()+1);
						if (nextByte != "-".getBytes()[0]) {
							// length is not negative, extract length
							int contentLength;
							try {
								buf.readBytes(out, buf.forEachByte(new Util.ByteArrayInBuf(RESULT_LENGTH_END))-buf.readerIndex()-1);
								contentLength = Integer.valueOf( new String(out.toByteArray()) );
								out.reset();
								
								buf.skipBytes(RESULT_LENGTH_END.length);
								// read as many bytes
								
								System.out.println(contentLength);
								buf.readBytes(out, contentLength);
								String content = new String(out.toByteArray());
								System.out.println(content);

								break;
							} catch (IOException e) {
								
							}
							
						} else {
							responses.add(new Response(null));
						}


					}
					
//				if (Arrays.equals( buf.readBytes(RESULT_LENGTH_BEGIN.length).array(), RESULT_LENGTH_BEGIN)) {
//				
//					// if the next byte is UTF8 "-"
//					if (!Arrays.equals(buf.readBytes(NO_RESULT.length).array(), NO_RESULT)) {
//					
//						ByteBuf in = PooledByteBufAllocator.DEFAULT.buffer();
//						// read up to RESULT_LENGTH_END
//						while(buf.isReadable()) {
//							in.writeByte(buf.readByte());
//							
//							if (in.forEachByte(new Util.ByteArrayInBuf(RESULT_LENGTH_END)) != -1) {
//								System.out.println("eek --"+new String(in.readBytes(in.readableBytes()-1).array())+"--");
//								in.clear();
//							}
//						}
//					
//					} else {
//						responses.add(new Response(null));
//					}
				
					
				
				}
				
				
				
				return responses;
				
				
			}
			
		};
		
	}

	
	
}
