package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Hincrby extends KeyedCommand<IntegerReply>{

	private final byte[] field;
	private final byte[] increment;	// careful, 64 bit max!
	
	public Hincrby(String key, byte[] field, byte[] increment) {
		super(key);
		this.field = field;
		this.increment = increment;
	}
	
	public byte[] getField() {
		return field;
	}
	
	public byte[] getIncrement() {
		return increment;
	}
}