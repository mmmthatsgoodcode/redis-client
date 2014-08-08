package com.mmmthatsgoodcode.redis.protocol.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.MultiBulkReply;

public class Mget extends AbstractCommand<MultiBulkReply>{

	private final List<String> keyList;
	
	public Mget(String key) {
		this.keyList = new ArrayList<String>();
		this.keyList.add(key);
	}
	
	public Mget(String... keys){
		this.keyList = Arrays.asList(keys);
	}

	public List<String> getKeyList() {
		return keyList;
	}
}
