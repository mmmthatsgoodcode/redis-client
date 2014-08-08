package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.BulkReply;

public class Rpop extends KeyedCommand<BulkReply>{

	public Rpop(String key) {
		super(key);
	}

}
