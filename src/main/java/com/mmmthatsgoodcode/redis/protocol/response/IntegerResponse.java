package com.mmmthatsgoodcode.redis.protocol.response;

import io.netty.buffer.ByteBuf;

import com.mmmthatsgoodcode.redis.protocol.Response;

public class IntegerResponse extends Response {

	public IntegerResponse(ByteBuf in) {
		super(in);
	}


	@Override
	public boolean decode() {
		return false;
	}	
	
}
