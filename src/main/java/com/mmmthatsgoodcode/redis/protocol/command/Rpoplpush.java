package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.BulkReply;

public class Rpoplpush extends KeyedCommand<BulkReply>{
	
	private final byte[] destination;

	public Rpoplpush(String key, byte[] destination) {
		super(key);
		this.destination = destination;
	}

	public byte[] getDestination() {
		return destination;
	}
}
