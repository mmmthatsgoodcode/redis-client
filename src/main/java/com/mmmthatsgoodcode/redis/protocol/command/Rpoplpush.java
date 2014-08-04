package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.BulkReply;

public class Rpoplpush extends AbstractCommand<BulkReply>{
	
	private final byte[] source;
	private final byte[] destination;

	public Rpoplpush(byte[] source, byte[] destination) {
		this.source = source;
		this.destination = destination;
	}

	public byte[] getSource() {
		return source;
	}
	
	public byte[] getDestination() {
		return destination;
	}
}
