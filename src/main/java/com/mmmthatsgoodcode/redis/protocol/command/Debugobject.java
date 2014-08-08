package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.StatusReply;

public class Debugobject extends KeyedCommand<StatusReply> {

	public Debugobject(String key) {
		super(key);
	}

	
}