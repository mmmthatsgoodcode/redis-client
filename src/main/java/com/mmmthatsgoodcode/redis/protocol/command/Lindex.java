package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.BulkReply;

public class Lindex extends KeyedCommand<BulkReply>{

	private final byte[] index;
	
	public Lindex(String key, byte[] index) {
		super(key);
		this.index = index;
	}

	public byte[] getIndex() {
		return index;
	}
}
