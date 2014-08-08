package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.BulkReply;

public class Echo extends KeyedCommand<BulkReply>{

	public Echo(String key) {
		super(key);
	}

}
