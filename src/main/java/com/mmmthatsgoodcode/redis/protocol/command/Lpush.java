package com.mmmthatsgoodcode.redis.protocol.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Lpush extends KeyedCommand<IntegerReply>{

	private final List<byte[]> valuesList;
	
	public Lpush(String key, byte[] value) {
		super(key);
		this.valuesList = new ArrayList<byte[]>();
		this.valuesList.add(value);
	}
	
	public Lpush(String key, byte[] value, byte[]... values) {
		super(key);
		this.valuesList = new ArrayList<byte[]>();
		this.valuesList.add(value);
		this.valuesList.addAll(Arrays.asList(values));
	}
	
	public List<byte[]> getValuesList() {
		return valuesList;
	}
	
}
