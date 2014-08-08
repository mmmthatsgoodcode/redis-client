package com.mmmthatsgoodcode.redis.protocol.command;

import java.util.Arrays;
import java.util.List;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Rpush extends KeyedCommand<IntegerReply>{

	private final List<String> values;
	public Rpush(String key, String... values) {
		super(key);
		this.values = Arrays.asList(values);
	}
	
	public List<String> getValues() {
		return values;
	}
}
