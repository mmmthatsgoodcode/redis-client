package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.BulkReply;

public class Lpop extends KeyedCommand<BulkReply>{

	public Lpop(String key) {
		super(key);
	}
}
