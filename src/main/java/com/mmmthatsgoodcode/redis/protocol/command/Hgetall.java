package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.MultiBulkReply;

public class Hgetall extends KeyedCommand<MultiBulkReply>{

	public Hgetall(String key) {
		super(key);
	}
}
