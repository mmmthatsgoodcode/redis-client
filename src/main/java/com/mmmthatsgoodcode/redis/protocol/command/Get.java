package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.BulkReply;

public class Get extends KeyedCommand<BulkReply> {

	public static final byte[] NAME = "GET".getBytes();
	
	public Get(String key) {
		super(key);
	}

}
