package com.mmmthatsgoodcode.redis.protocol.request;

import io.netty.buffer.ByteBuf;

import com.mmmthatsgoodcode.redis.protocol.Request;
import com.mmmthatsgoodcode.redis.protocol.Request.EncodeHelper;

public class Ping extends Request {

	public static final byte[] NAME = "PING".getBytes();
	
	@Override
	public ByteBuf encode() {
		EncodeHelper out = new EncodeHelper(byteBufAllocator.buffer());
		out.addArgc(1);
		out.addArg(NAME);
		return out.buffer();
	}

}
