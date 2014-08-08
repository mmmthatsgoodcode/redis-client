package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.BulkReply;

public class Dump extends KeyedCommand<BulkReply>{

	public Dump(String key) {
		super(key);
	}
	
}
