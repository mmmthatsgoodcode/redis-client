package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Hlen extends KeyedCommand<IntegerReply>{

	public Hlen(String key) {
		super(key);
	}
	
}
