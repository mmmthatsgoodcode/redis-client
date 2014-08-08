package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.BulkReply;

public class Type extends KeyedCommand<BulkReply>{

	public Type(String key) {
		super(key);
	}

}
