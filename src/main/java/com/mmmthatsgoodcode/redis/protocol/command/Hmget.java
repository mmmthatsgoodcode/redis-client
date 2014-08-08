package com.mmmthatsgoodcode.redis.protocol.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.MultiBulkReply;

public class Hmget extends KeyedCommand<MultiBulkReply>{

	private final List<byte[]> fieldList;
	
	public Hmget(String key, byte[] field) {
		super(key);
		this.fieldList = new ArrayList<byte[]>();
		this.fieldList.add(field);
	}
	
	public Hmget(String key, byte[]... fields) {
		super(key);
		this.fieldList = Arrays.asList(fields);
	}

	public List<byte[]> getFieldList() {
		return fieldList;
	}
}
