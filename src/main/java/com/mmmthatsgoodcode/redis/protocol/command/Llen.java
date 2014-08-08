package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Llen extends KeyedCommand<IntegerReply>{

	public Llen(String key) {
		super(key);
	}
}
