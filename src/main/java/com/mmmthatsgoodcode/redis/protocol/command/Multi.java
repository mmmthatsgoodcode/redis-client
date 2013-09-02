package com.mmmthatsgoodcode.redis.protocol.command;

import io.netty.buffer.ByteBuf;

import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand;
import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand.EncodeHelper;
import com.mmmthatsgoodcode.redis.protocol.reply.StatusReply;

public class Multi extends AbstractCommand<StatusReply> {

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
