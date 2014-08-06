package com.mmmthatsgoodcode.redis.protocol.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Sinterstore extends AbstractCommand<IntegerReply>{

	private final String destination;
	private final List<String> keyList;
	
	public Sinterstore(String destination, String key) {
		this.destination = destination;
		this.keyList = new ArrayList<String>();
		this.keyList.add(key);
	}
	public Sinterstore(String destination, String key, String... keys) {
		this.destination = destination;
		List<String> tmp = new ArrayList<String>();
		tmp.add(key);
		tmp.addAll(Arrays.asList(keys));
		this.keyList = tmp;
	}
	
	public String getDestination() {
		return destination;
	}
	
	public List<String> getKeyList() {
		return keyList;
	}
}
