package com.mmmthatsgoodcode.redis.protocol.request;

import io.netty.buffer.ByteBuf;

import com.mmmthatsgoodcode.redis.protocol.Request;

public class Set extends Request {

	private static final byte[] NAME = "SET".getBytes(ENCODING);
	
	private String key;
	private byte[] value;
	
	public Set(String key, String value) {
		this(key, value.getBytes(ENCODING));
	}
	
	public Set(String key, byte[] value) {
		this.key = key;
		this.value = value;
	}
	
	@Override
	public ByteBuf encode() {
		EncodeHelper out = new EncodeHelper(byteBufAllocator.buffer());
		out.addArgc(3);
		out.addArg(NAME);
		out.addArg(this.key.getBytes(ENCODING));
		out.addArg(this.value);

		return out.buffer();
	}

}
