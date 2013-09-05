package com.mmmthatsgoodcode.redis.protocol.command;

import io.netty.buffer.ByteBuf;

import com.mmmthatsgoodcode.redis.protocol.AbstractCommand;
import com.mmmthatsgoodcode.redis.protocol.AbstractCommand.EncodeHelper;
import com.mmmthatsgoodcode.redis.protocol.reply.MultiBulkReply;

public class Exec extends AbstractCommand<MultiBulkReply> {

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
