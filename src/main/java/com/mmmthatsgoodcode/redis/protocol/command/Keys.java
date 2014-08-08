package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.MultiBulkReply;

public class Keys extends KeyedCommand<MultiBulkReply>{

	public Keys(String key) {
		super(key);
	}

}
