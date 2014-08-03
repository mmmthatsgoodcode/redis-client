package com.mmmthatsgoodcode.redis.protocol.command;

import java.util.Arrays;
import java.util.List;

import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand;
import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;
import com.mmmthatsgoodcode.redis.protocol.reply.MultiBulkReply;

public class Del extends AbstractCommand<IntegerReply>{

	private final List<String> keys;
	
	public Del(String... keys) {
		this.keys = Arrays.asList(keys);
	}
	
	public List<String> getKeys(){
		return this.keys;
	}
}
