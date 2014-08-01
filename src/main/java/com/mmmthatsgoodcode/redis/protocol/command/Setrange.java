package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Setrange extends KeyedCommand<IntegerReply>{
	
	private final byte[] offset;
	private final byte[] value;

	public Setrange(String key,byte[] offset, byte[] value) {
		super(key);
		this.offset = offset;
		this.value = value;
	}
	
	public byte[] getOffset() {
		return offset;
	}
	
	public byte[] getValue() {
		return value;
	}
}
