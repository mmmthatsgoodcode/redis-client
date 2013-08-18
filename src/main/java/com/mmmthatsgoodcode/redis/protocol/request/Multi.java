package com.mmmthatsgoodcode.redis.protocol.request;

import io.netty.buffer.ByteBuf;

import com.mmmthatsgoodcode.redis.protocol.Request;
import com.mmmthatsgoodcode.redis.protocol.Request.EncodeHelper;
import com.mmmthatsgoodcode.redis.protocol.response.StatusResponse;

public class Multi extends Request<StatusResponse> {

	private static final byte[] NAME = "MULTI".getBytes(ENCODING);
	
	public Multi() {
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
