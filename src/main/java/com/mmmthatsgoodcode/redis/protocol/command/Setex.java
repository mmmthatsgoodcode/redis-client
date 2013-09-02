package com.mmmthatsgoodcode.redis.protocol.command;

import io.netty.buffer.ByteBuf;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand.EncodeHelper;

public class Setex extends Set {

	private final int expiry;
	private static final byte[] NAME = "SETEX".getBytes(ENCODING);
	
	public Setex(String key, String value, int expiry) {
		super(key, value);
		setArgc(4);
		this.expiry = expiry;
	}
	
	public int getExpiry() {
		return expiry;
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
