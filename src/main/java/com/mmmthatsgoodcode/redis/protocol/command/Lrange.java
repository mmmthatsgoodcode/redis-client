package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.MultiBulkReply;

public class Lrange extends KeyedCommand<MultiBulkReply>{

	private final byte[] start;
	private final byte[] stop;
	
	public Lrange(String key, byte[] start, byte[] stop) {
		super(key);
		this.start = start;
		this.stop = stop;
	}
	
	public byte[] getStart() {
		return start;
	}
	
	public byte[] getStop() {
		return stop;
	}
}
