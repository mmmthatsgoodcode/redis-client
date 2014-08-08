package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Hsetnx extends KeyedCommand<IntegerReply>{

	private final byte[] field;
	private final byte[] value;
	
	public Hsetnx(String key, byte[] field, byte[] value) {
		super(key);
		this.field = field;
		this.value = value;
	}
	
	public byte[] getField() {
		return field;
	}
	
	public byte[] getValue() {
		return value;
	}
}
