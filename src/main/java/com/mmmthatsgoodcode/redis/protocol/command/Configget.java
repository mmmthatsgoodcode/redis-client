package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.MultiBulkReply;

public class Configget extends AbstractCommand<MultiBulkReply> {
	// TODO try it. There seems to be an error in the doc. It says the reply type is Bulk string reply, but when you try it on a client, you 
	// get a multiBulk reply.
	private final byte[] parameter;
	
	public Configget(byte[] parameter){
		this.parameter = parameter;
	}
	
	public byte[] getParameter() {
		return parameter;
	}
}
