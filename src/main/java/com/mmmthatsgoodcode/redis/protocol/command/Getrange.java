package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.BulkReply;

public class Getrange extends KeyedCommand<BulkReply>{
	
	private byte[] start;
	private byte[] end;

	public Getrange(String key, byte[] start, byte[] end) {
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
