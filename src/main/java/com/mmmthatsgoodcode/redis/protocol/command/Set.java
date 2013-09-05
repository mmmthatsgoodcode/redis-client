package com.mmmthatsgoodcode.redis.protocol.command;

import io.netty.buffer.ByteBuf;

import com.mmmthatsgoodcode.redis.protocol.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.AbstractCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.StatusReply;

public class Set extends KeyedCommand<StatusReply> {

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
		out.addArg(getName());
		out.addArg(getKey().getBytes(ENCODING));
		out.addArg(getValue());

		return out.buffer();
	}

	@Override
	public byte[] getName() {
		return NAME;
	}
	
	public byte[] getValue() {
		return value;
	}


}
