package com.mmmthatsgoodcode.redis.protocol.response;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;

import com.mmmthatsgoodcode.redis.protocol.Response;

public class StatusResponse extends Response<String> {

	
	
	public StatusResponse(ByteBuf in) {
		super(in);
	}

	/**
	 * Expected format:
	 * +{status response}DELIMITER
	 */
	@Override
	public boolean decode() {
		
		// there is at least one delimiter in the buffer
		if (this.in.forEachByte(ByteBufProcessor.FIND_CRLF) != -1) {
			byte[] statusCode = this.in.readBytes( this.in.forEachByte(ByteBufProcessor.FIND_CRLF) - this.in.readerIndex() ).array(); // read up to the new line..
			setValue(new String(statusCode));
			return true;
		}
		
		return false;
	}	
	

}
