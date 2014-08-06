package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.MultiBulkReply;

public class Smembers extends KeyedCommand<MultiBulkReply> {

	public Smembers(String key) {
		super(key);
	}

}
