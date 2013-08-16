package com.mmmthatsgoodcode.redis.protocol.request;

import io.netty.buffer.ByteBuf;

import com.mmmthatsgoodcode.redis.protocol.KeyedRequest;
import com.mmmthatsgoodcode.redis.protocol.Request;

public class Set extends KeyedRequest {

	private static final byte[] NAME = "SET".getBytes(ENCODING);
	private byte[] value;
	
	public Set(String key, String value) {
		this(key, value.getBytes(ENCODING));
		setArgc(3);
	}
	
	public Set(String key, byte[] value) {
		super(key);
		this.value = value;
	}
	
	@Override
	public ByteBuf encode() {
		EncodeHelper out = new EncodeHelper(byteBufAllocator.buffer());
		out.addArg(NAME);
		out.addArg(key.getBytes(ENCODING));
		out.addArg(value);

		return out.buffer();
	}

	@Override
	public byte[] getName() {
		return NAME;
	}
	
	@Override
	public boolean canPipe() {
		return true;
	}

}
