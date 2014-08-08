package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.Redis2TextProtocol;
import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.BulkReply;

public class Info extends AbstractCommand<BulkReply>{

	private final byte[] section;
	
	public Info(){
		this.section = "default".getBytes(Redis2TextProtocol.ENCODING);
	}
	public Info(byte[] section){
		this.section = section;
	}
	
	public byte[] getSection() {
		return section;
	}
}
