package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Pexpireat extends KeyedCommand<IntegerReply> {

	private final byte[] timestamp;
	
	public Pexpireat(String key, byte[] timestamp) {
		super(key);
		this.timestamp = timestamp;
	}

	public byte[] getTimestamp() {
		return timestamp;
	}
}
