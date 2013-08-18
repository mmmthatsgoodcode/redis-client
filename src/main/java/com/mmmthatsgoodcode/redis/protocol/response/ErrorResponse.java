package com.mmmthatsgoodcode.redis.protocol.response;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;

import com.mmmthatsgoodcode.redis.Util;
import com.mmmthatsgoodcode.redis.protocol.Response;

public class ErrorResponse extends Response<String> {

	public ErrorResponse(ByteBuf in) {
		super(in);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Expected format:
	 * -{error type} {error message}DELIMITER
	 */
	@Override
	public boolean decode() {
		
		if (this.in.forEachByte(HAS_DELIMITER) != -1) {
			// there is a delimiter in this, we're good to parse
			byte[] errType = this.in.readBytes( this.in.forEachByte(ByteBufProcessor.FIND_LINEAR_WHITESPACE)-this.in.readerIndex() ).array(); // read up to the first white space
//			System.out.println("Err code: "+new String(errType));
			// move reader beyond the whitespace
			byte[] errMessage = this.in.readBytes( this.in.forEachByte(HAS_DELIMITER)-this.in.readerIndex() ).array(); // read up to the next white space
			setValue(new String(errType, ENCODING)+": "+new String(errMessage, ENCODING));
			
			return true;
		}
		
		return false;
	}	
	
}
