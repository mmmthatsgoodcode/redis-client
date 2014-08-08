package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Publish extends AbstractCommand<IntegerReply> {

	private final byte[] channel;
	private final byte[] message;
	
	public Publish(byte[] channel, byte[] message){
		this.channel = channel;
		this.message = message;
	}
	
	public byte[] getChannel() {
		return channel;
	}
	
	public byte[] getMessage() {
		return message;
	}
}
