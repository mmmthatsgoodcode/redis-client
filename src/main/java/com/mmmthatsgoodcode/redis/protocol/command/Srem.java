package com.mmmthatsgoodcode.redis.protocol.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Srem extends KeyedCommand<IntegerReply> {

	private final List<byte[]> MemberList;
	
	public Srem(String key, byte[] member) {
		super(key);
		this.MemberList = new ArrayList<byte[]>();
		this.MemberList.add(member);
	}
	
	public Srem(String key, byte[] member, byte[]... members) {
		super(key);
		List<byte[]> tmp = new ArrayList<byte[]>();
		tmp.add(member);
		tmp.addAll(Arrays.asList(members));
		this.MemberList = tmp;
	}

	public List<byte[]> getMemberList() {
		return MemberList;
	}
}
