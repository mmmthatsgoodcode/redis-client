package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Expire extends KeyedCommand<IntegerReply>{
	
	private byte[] value;

	public Expire(String key, byte[] value) {
		super(key);
		this.value = value;
	}
	
	public byte[] getValue(){
		return this.value;
	}

}
