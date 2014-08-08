package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.MultiBulkReply;

public class Hkeys extends KeyedCommand<MultiBulkReply>{

	public Hkeys(String key) {
		super(key);
	}

}
