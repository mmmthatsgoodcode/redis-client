package com.mmmthatsgoodcode.redis.protocol.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Sunionstore extends AbstractCommand<IntegerReply> {

	private final String destination;
	private final List<String> keyList;
	
	public Sunionstore(String destination, String key) {
		this.destination = destination;
		this.keyList = new ArrayList<String>();
		this.keyList.add(key);
	}
	
	public Sunionstore(String destination, String key, String... keys) {
		this.destination = destination;
		this.keyList = new ArrayList<String>();
		this.keyList.add(key);
		this.keyList.addAll(Arrays.asList(keys));
	}
	
	public String getDestination() {
		return destination;
	}
	
	public List<String> getKeyList() {
		return keyList;
	}
}
