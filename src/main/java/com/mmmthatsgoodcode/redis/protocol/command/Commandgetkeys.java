package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.MultiBulkReply;

public class Commandgetkeys extends AbstractCommand<MultiBulkReply>{
	
	private final com.mmmthatsgoodcode.redis.protocol.Command command;
	
	public Commandgetkeys(com.mmmthatsgoodcode.redis.protocol.Command command){
		this.command = command;
	}
	
	public com.mmmthatsgoodcode.redis.protocol.Command getCommand() {
		return command;
	}
}
