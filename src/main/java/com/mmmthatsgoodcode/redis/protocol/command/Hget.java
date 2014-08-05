package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.BulkReply;

public class Hget extends KeyedCommand<BulkReply>{

	private final byte[] field;
	
	public Hget(String key, byte[] field) {
		super(key);
		this.field = field;
	}
	
	public byte[] getField() {
		return field;
	}
}
