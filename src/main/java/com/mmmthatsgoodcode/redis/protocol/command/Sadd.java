package com.mmmthatsgoodcode.redis.protocol.command;

import java.util.List;
import java.util.Arrays;

import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Sadd extends AbstractCommand<IntegerReply>{
	
	private final List<String> keys;

	public Sadd(String... keys) {
		this.keys = Arrays.asList(keys);
	}

	public List<String> getKeys() {
		return keys;
	}
}
