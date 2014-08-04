package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.Redis2TextProtocol;
import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Bitcount extends KeyedCommand<IntegerReply>{
	
	private final byte[] start;
	private final byte[] end;

	public Bitcount(String key) {
		super(key);
		this.start = "0".getBytes(Redis2TextProtocol.ENCODING);
		this.end = "-1".getBytes(Redis2TextProtocol.ENCODING);
	}
	
	public Bitcount(String key, byte[] start, byte[] end) {
		super(key);
		this.start = start;
		this.end = end;
	}
	
	public byte[] getStart() {
		return start;
	}
	
	public byte[] getEnd() {
		return end;
	}
}