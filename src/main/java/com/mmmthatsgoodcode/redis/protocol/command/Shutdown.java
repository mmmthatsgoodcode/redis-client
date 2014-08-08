package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.StatusReply;

public class Shutdown extends AbstractCommand<StatusReply>{

	private final byte[] action;
	
	public Shutdown(byte[] action){
		this.action = action;
	}
	
	public byte[] getAction() {
		return action;
	}
}
