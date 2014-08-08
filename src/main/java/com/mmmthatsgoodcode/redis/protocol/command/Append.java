package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Append extends KeyedCommand<IntegerReply>{

	private final byte[] value;
	
	public Append(String key, byte[] value) {
		super(key);
		this.value = value;
	}

	
	public byte[] getValue() {
		return value;
	}
}
