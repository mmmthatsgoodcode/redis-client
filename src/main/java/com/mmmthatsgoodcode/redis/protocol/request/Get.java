package com.mmmthatsgoodcode.redis.protocol.request;

import io.netty.buffer.ByteBuf;

import com.mmmthatsgoodcode.redis.protocol.Request;

public class Get extends Request {

	public static final byte[] NAME = "GET".getBytes();
	private String id;
	
	public Get(String id) {
		this.id = id;
	}
	
	@Override
	public ByteBuf encode() {
		EncodeHelper out = new EncodeHelper(byteBufAllocator.buffer());
		out.addArgc(2);
		out.addArg(NAME);
		out.addArg(id.getBytes(ENCODING));
		return out.buffer();
	}

}
