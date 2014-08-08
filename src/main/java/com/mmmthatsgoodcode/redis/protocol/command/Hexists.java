package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Hexists extends KeyedCommand<IntegerReply>{

	private final byte[] field;
	
	public Hexists(String key, byte[] field) {
		super(key);
		this.field = field;
	}
	
	public byte[] getField() {
		return field;
	}
}
