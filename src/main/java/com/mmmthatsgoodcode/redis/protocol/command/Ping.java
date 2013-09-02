package com.mmmthatsgoodcode.redis.protocol.command;

import io.netty.buffer.ByteBuf;

import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand;
import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand.EncodeHelper;
import com.mmmthatsgoodcode.redis.protocol.reply.StatusReply;

public class Ping extends AbstractCommand<StatusReply> {

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
