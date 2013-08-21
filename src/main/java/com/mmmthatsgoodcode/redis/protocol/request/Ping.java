package com.mmmthatsgoodcode.redis.protocol.request;

import io.netty.buffer.ByteBuf;

import com.mmmthatsgoodcode.redis.protocol.AbstractRequest;
import com.mmmthatsgoodcode.redis.protocol.AbstractRequest.EncodeHelper;
import com.mmmthatsgoodcode.redis.protocol.response.StatusResponse;

public class Ping extends AbstractRequest<StatusResponse> {

	public static final byte[] NAME = "PING".getBytes();
	
	public Ping() {
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
