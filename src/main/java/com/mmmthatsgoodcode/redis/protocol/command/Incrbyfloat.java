package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.BulkReply;

public class Incrbyfloat extends KeyedCommand<BulkReply>{

	private final byte[] increment;
	
	public Incrbyfloat(String key, byte[] increment) {
		super(key);
		this.increment = increment;
	}
	
	public byte[] getIncrement() {
		return increment;
	}
}
