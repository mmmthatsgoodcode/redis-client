package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Expire extends KeyedCommand<IntegerReply>{
	
	private final byte[] seconds;

	public Expire(String key, byte[] seconds) {
		super(key);
		this.seconds = seconds;
	}
	
	public byte[] getSeconds(){
		return this.seconds;
	}

}
