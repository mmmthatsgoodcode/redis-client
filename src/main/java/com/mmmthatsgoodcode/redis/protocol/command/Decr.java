package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Decr extends KeyedCommand<IntegerReply>{

	public Decr(String key) {
		super(key);
	}

}
