/*package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;

public class Zrank extends KeyedCommand<Reply>{
	// TODO problem with the return value: 
	//		If member exists in the sorted set, Integer reply: the rank of member.
    //		If member does not exist in the sorted set or key does not exist, Bulk string reply: nil.

	private final byte[] member;
	
	public Zrank(String key, byte[] member) {
		super(key);
		this.member = member;
	}

	public byte[] getMember(){
		return this.member;
	}
}
*/