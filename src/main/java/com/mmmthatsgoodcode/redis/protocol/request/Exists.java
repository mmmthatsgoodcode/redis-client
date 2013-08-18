package com.mmmthatsgoodcode.redis.protocol.request;

import io.netty.buffer.ByteBuf;

import com.mmmthatsgoodcode.redis.protocol.KeyedRequest;
import com.mmmthatsgoodcode.redis.protocol.Request.EncodeHelper;
import com.mmmthatsgoodcode.redis.protocol.response.IntegerResponse;

public class Exists extends KeyedRequest<IntegerResponse> {

	public static final byte[] NAME = "EXISTS".getBytes();

	public Exists(String key) {
		super(key);
		setArgc(2);
	}

	@Override
	public ByteBuf encode() {
		EncodeHelper out = new EncodeHelper(byteBufAllocator.buffer());
		out.addArg(getName());
		out.addArg(key.getBytes(ENCODING));
		return out.buffer();
	}

	@Override
	public byte[] getName() {
		return NAME;
	}

}
