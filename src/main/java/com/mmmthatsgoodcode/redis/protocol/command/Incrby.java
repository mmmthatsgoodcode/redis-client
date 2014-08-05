package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Incrby extends KeyedCommand<IntegerReply>{

	private final byte[] increment;
	
	public Incrby(String key, byte[] increment) {
		super(key);
		this.increment = increment;
	}
	
	public byte[] getIncrement() {
		return increment;
	}
}
