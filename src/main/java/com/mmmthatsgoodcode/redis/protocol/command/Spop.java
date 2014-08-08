package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.BulkReply;

public class Spop extends KeyedCommand<BulkReply> {

	public Spop(String key) {
		super(key);
	}
}
