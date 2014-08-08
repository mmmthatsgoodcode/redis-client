package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.StatusReply;

public class Ltrim extends KeyedCommand<StatusReply>{

	private final byte[] start;
	private final byte[] stop;
	
	public Ltrim(String key, byte[] start, byte[] stop) {
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
