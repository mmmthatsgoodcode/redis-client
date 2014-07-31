package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.StatusReply;

public class Set extends KeyedCommand<StatusReply> {

	private byte[] value;
	
	public Set(String key, byte[] value) {
		super(key);
		this.value = value;
	}
	
	public byte[] getValue() {
		return value;
	}


}
