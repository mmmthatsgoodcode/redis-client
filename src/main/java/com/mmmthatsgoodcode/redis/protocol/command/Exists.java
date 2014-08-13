package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Exists extends KeyedCommand<IntegerReply> {

	public Exists(String key) {
		super(key);
	}

}
