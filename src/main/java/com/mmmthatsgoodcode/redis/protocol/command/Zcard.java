package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Zcard extends KeyedCommand<IntegerReply> {

	public Zcard(String key) {
		super(key);
	}

}
