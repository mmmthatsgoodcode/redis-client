package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Rpushx extends KeyedCommand<IntegerReply>{

	private final String value;
	
	public Rpushx(String key, String value) {
		super(key);
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
