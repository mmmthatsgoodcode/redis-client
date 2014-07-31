package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Decrby extends KeyedCommand<IntegerReply>{
	
	private byte[] decrement;

	public Decrby(String key, byte[] decrement) {
		super(key);
		this.decrement = decrement;
	}
	
	public byte[] getDecrement() {
		return decrement;
	}
}
