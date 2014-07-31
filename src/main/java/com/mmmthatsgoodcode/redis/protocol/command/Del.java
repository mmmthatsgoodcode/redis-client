package com.mmmthatsgoodcode.redis.protocol.command;

import java.util.Arrays;
import java.util.List;

import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand;
<<<<<<< HEAD
=======
import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
>>>>>>> 23579b4... fixes #14 with Final added to the Entities' variables
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Del extends AbstractCommand<IntegerReply>{

	private final List<String> keys;
	
	public Del(String... keys) {
		this.keys = Arrays.asList(keys);
	}
	
<<<<<<< HEAD
	public List<String> getKeys() {
		return keys;
=======
	public List<String> getKeys(){
		return this.keys;
>>>>>>> 23579b4... fixes #14 with Final added to the Entities' variables
	}
}
