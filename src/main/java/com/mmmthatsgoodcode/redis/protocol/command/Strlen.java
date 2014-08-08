package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Strlen extends KeyedCommand<IntegerReply> {

	public Strlen(String key) {
		super(key);
	}

}
