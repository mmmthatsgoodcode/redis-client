package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.BulkReply;

public class Select extends KeyedCommand<BulkReply>{

	public Select(String key) {
		super(key);
	}

}
