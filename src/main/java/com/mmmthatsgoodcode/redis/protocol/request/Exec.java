package com.mmmthatsgoodcode.redis.protocol.request;

import io.netty.buffer.ByteBuf;

import com.mmmthatsgoodcode.redis.protocol.Request;
import com.mmmthatsgoodcode.redis.protocol.Request.EncodeHelper;
import com.mmmthatsgoodcode.redis.protocol.response.MultiBulkResponse;

public class Exec extends Request<MultiBulkResponse> {

	private static final byte[] NAME = "EXEC".getBytes(ENCODING);
	
	public Exec() {
		setArgc(1);
	}
	
	@Override
	public ByteBuf encode() {
		EncodeHelper out = new EncodeHelper(byteBufAllocator.buffer());
		out.addArg(getName());
		return out.buffer();
	}

	@Override
	public byte[] getName() {
		return NAME;
	}

}
