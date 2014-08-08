package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Expireat extends KeyedCommand<IntegerReply>{
	
	private final byte[] timeStamp;

	public Expireat(String key, byte[] timeStamp) {
		super(key);
		this.timeStamp = timeStamp;
	}
	
	public byte[] getTimeStamp(){
		return this.timeStamp;
	}

}
