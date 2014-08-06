package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Sismember extends KeyedCommand<IntegerReply> {

	private final byte[] member;
	
	public Sismember(String key, byte[] member) {
		super(key);
		this.member = member;
	}

	public byte[] getMember() {
		return member;
	}
}
