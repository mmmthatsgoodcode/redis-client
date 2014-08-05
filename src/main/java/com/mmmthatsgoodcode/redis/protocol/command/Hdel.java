package com.mmmthatsgoodcode.redis.protocol.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Hdel extends KeyedCommand<IntegerReply>{
	
	private final List<byte[]> fieldsList;

	public Hdel(String key, byte[] field) {
		super(key);
		List<byte[]> tmp = new ArrayList<byte[]>();
		tmp.add(field);
		this.fieldsList = tmp; 
	}
	
	public Hdel(String key, byte[]... fields) {
		super(key);
		this.fieldsList = Arrays.asList(fields);
	}
	
	 public List<byte[]> getFieldsList() {
		return fieldsList;
	}
}
