package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Scard extends KeyedCommand<IntegerReply>{

	public Scard(String key) {
		super(key);
	}

}
