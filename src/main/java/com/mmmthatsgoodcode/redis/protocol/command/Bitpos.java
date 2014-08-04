package com.mmmthatsgoodcode.redis.protocol.command;

import java.util.Arrays;
import java.util.List;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Bitpos extends KeyedCommand<IntegerReply>{

	private final List<byte[]> bits;
	
	public Bitpos(String key, byte[]... bits) {
		super(key);
		this.bits = Arrays.asList(bits);
	}
	
	public List<byte[]> getBits() {
		return bits;
	}
}
