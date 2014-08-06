package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Smove extends AbstractCommand<IntegerReply> {

	private final String source;
	private final String destination;
	private final byte[] member;
	
	public Smove(String source, String destination, byte[] member) {
		this.source = source;
		this.destination = destination;
		this.member = member;
	}
	
	public String getSource() {
		return source;
	}
	
	public String getDestination() {
		return destination;
	}
	
	public byte[] getMember() {
		return member;
	}
}
