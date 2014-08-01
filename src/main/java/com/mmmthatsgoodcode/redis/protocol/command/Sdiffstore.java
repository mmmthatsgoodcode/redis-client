package com.mmmthatsgoodcode.redis.protocol.command;

import java.util.Arrays;
import java.util.List;

import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Sdiffstore extends AbstractCommand<IntegerReply>{
	
	private final List<String> keys;

	public Sdiffstore(String... keys){
		this.keys = Arrays.asList(keys);
	}
	
	public List<String> getKeys() {
		return keys;
	}
}
