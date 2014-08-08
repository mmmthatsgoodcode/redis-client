package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Pexpire extends KeyedCommand<IntegerReply>{

	private final byte[] time;
	
	public Pexpire(String key, byte[] time) {
		super(key);
		this.time = time;
	}

	public byte[] getTime() {
		return time;
	}
}
