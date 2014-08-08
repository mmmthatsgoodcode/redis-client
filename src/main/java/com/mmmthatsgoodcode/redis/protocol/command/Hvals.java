package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.MultiBulkReply;

public class Hvals  extends KeyedCommand<MultiBulkReply>{

	public Hvals(String key) {
		super(key);
	}
}
