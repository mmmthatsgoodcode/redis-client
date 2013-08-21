package com.mmmthatsgoodcode.redis.protocol.request;

import io.netty.buffer.ByteBuf;

import com.mmmthatsgoodcode.redis.protocol.KeyedRequest;
import com.mmmthatsgoodcode.redis.protocol.AbstractRequest.EncodeHelper;

public class Setex extends Set {

	private final int expiry;
	private static final byte[] NAME = "SETEX".getBytes(ENCODING);
	
	public Setex(String key, String value, int expiry) {
		super(key, value);
		setArgc(4);
		this.expiry = expiry;
	}
	
	@Override
	public ByteBuf encode() {
		EncodeHelper out = new EncodeHelper(byteBufAllocator.buffer());
		out.addArg(getName());
		out.addArg(getKey().getBytes(ENCODING));
		out.addArg(String.valueOf(expiry).getBytes(ENCODING));
		out.addArg(getValue());
		
		return out.buffer();
	}

	@Override
	public byte[] getName() {
		return NAME;
	}

}
