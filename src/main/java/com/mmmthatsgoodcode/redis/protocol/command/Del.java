package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;
import com.mmmthatsgoodcode.redis.protocol.reply.MultiBulkReply;

public class Del extends KeyedCommand<IntegerReply>{

	public Del(String key) {
		super(key);
		//TODO how to support multiple keys? are all the keys in the string? how will redis' server understand it?
	}
	
}
