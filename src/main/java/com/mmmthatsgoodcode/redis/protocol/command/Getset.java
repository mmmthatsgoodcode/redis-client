package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.BulkReply;

public class Getset extends KeyedCommand<BulkReply>{
	
	private final byte[] value;

	public Getset(String key, byte[] value) {
		super(key);
		this.value = value;
	}
	
	public byte[] getValue() {
		return value;
	}
}
