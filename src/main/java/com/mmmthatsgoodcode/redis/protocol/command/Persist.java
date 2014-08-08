package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Persist extends KeyedCommand<IntegerReply>{

	public Persist(String key) {
		super(key);
	}

}
