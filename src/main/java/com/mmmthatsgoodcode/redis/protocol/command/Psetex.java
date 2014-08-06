package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.StatusReply;

public class Psetex extends KeyedCommand<StatusReply> {

	private final byte[] time;
	private final byte[] value;
	
	public Psetex(String key, byte[] time, byte[] value) {
		super(key);
		this.time = time;
		this.value = value;
	}

	public byte[] getTime() {
		return time;
	}
	
	public byte[] getValue() {
		return value;
	}
}
