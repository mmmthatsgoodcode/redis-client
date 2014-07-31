package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Incr extends KeyedCommand<IntegerReply>{

	public Incr(String key) {
		super(key);
	}

}
