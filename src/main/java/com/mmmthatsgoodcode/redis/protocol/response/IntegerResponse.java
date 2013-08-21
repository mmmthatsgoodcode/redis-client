package com.mmmthatsgoodcode.redis.protocol.response;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;

import com.mmmthatsgoodcode.redis.protocol.AbstractResponse;

public class IntegerResponse extends AbstractResponse<Integer> {

	public IntegerResponse(ByteBuf in) {
		super(in);
	}


	/**
	 * Expected format:
	 * :{integer-as-string}DELIMITER
	 */
	@Override
	public boolean decode() {
		
		if (this.in.forEachByte(HAS_DELIMITER) != -1) {
			// there is a delimiter in this, we're good to parse
			byte[] intValue = this.in.readBytes( this.in.forEachByte(HAS_DELIMITER)-this.in.readerIndex() ).array(); 
			setValue(Integer.valueOf(new String(intValue, ENCODING)));
			
			return true;
		}
		
		return false;
	}	
	
}
