package com.mmmthatsgoodcode.redis.protocol.request;

import io.netty.buffer.ByteBuf;

import com.mmmthatsgoodcode.redis.protocol.Request;
import com.mmmthatsgoodcode.redis.protocol.Request.EncodeHelper;

public class Ping extends Request {

	public static final byte[] NAME = "PING".getBytes();
	
	public Ping() {
		setArgc(1);
	}
	
	@Override
	public ByteBuf encode() {
		EncodeHelper out = new EncodeHelper(byteBufAllocator.buffer());
		out.addArg(NAME);
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
