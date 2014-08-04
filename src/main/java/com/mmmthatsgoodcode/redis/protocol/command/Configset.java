package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.StatusReply;

public class Configset extends AbstractCommand<StatusReply>{

	private final byte[] parameter;
	private final byte[] value;
	
	public Configset(byte[] param, byte[] value){
		this.parameter = param;
		this.value = value;
	}
	
	public byte[] getParameter() {
		return parameter;
	}
	
	public byte[] getValue() {
		return value;
	}
}
