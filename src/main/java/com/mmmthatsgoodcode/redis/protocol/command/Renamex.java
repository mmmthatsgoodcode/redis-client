package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Renamex extends KeyedCommand<IntegerReply>{

	private final String newKey;
	public Renamex(String oldKey, String newKey) {
		super(oldKey);
		this.newKey = newKey;
	}

	public String getNewKey() {
		return newKey;
	}
}
