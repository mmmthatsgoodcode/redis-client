package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Lrem extends KeyedCommand<IntegerReply>{

	private final byte[] count;
	private final byte[] value;
	
	public Lrem(String key, byte[] count, byte[] value) {
		super(key);
		this.count = count;
		this.value = value;
	}

	public byte[] getCount() {
		return count;
	}
	
	public byte[] getValue() {
		return value;
	}
}
