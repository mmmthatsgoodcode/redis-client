package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Pttl extends KeyedCommand<IntegerReply>{

	public Pttl(String key) {
		super(key);
	}
}
