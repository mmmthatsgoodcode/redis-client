package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.StatusReply;

public class Rename extends KeyedCommand<StatusReply>{

	private final String newKey;
	public Rename(String oldKey, String newKey) {
		super(oldKey);
		this.newKey = newKey;
	}

	public String getNewKey() {
		return newKey;
	}
}
