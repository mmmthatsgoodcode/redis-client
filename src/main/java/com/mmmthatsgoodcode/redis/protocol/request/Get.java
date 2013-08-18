package com.mmmthatsgoodcode.redis.protocol.request;

import io.netty.buffer.ByteBuf;

import com.mmmthatsgoodcode.redis.protocol.KeyedRequest;
import com.mmmthatsgoodcode.redis.protocol.Request;
import com.mmmthatsgoodcode.redis.protocol.response.BulkResponse;

public class Get extends KeyedRequest<BulkResponse> {

	public static final byte[] NAME = "GET".getBytes();
	
	public Get(String key) {
		super(key);
		setArgc(2);

	}
	
	@Override
	public ByteBuf encode() {
		EncodeHelper out = new EncodeHelper(byteBufAllocator.buffer());
		out.addArg(NAME);
		out.addArg(key.getBytes(ENCODING));
		return out.buffer();
	}

	@Override
	public byte[] getName() {
		return NAME;
	}


}
