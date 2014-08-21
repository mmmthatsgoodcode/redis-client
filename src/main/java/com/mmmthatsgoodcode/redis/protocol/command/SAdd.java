package com.mmmthatsgoodcode.redis.protocol.command;

import java.util.ArrayList;
import java.util.List;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class SAdd extends KeyedCommand<IntegerReply> {

	private final List<byte[]> members;
	
	public SAdd(String key, byte[] member) {
		super(key);
		this.members = new ArrayList<byte[]>();
		this.members.add(member);
	}
	
	public SAdd(String key, List<byte[]> members) {
		super(key);
		this.members = members;
	}
	
	public List<byte[]> getMembers(){
		return members;
	}
	
	public String toString(){
		return "SAdd";
	}
}
