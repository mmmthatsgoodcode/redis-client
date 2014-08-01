package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Ttl extends KeyedCommand<IntegerReply>{

	public Ttl(String key) {
		super(key);
	}

}
