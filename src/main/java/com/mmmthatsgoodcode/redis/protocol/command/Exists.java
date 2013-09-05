package com.mmmthatsgoodcode.redis.protocol.command;

import io.netty.buffer.ByteBuf;

import com.mmmthatsgoodcode.redis.protocol.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.AbstractCommand.EncodeHelper;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Exists extends KeyedCommand<IntegerReply> {

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
