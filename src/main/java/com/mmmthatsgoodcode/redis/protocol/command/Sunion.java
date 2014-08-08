package com.mmmthatsgoodcode.redis.protocol.command;

import java.util.ArrayList;
import java.util.List;

import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.MultiBulkReply;

public class Sunion extends AbstractCommand<MultiBulkReply> {
	
	private final List<String> keyList;
	
	public Sunion (String key, String...keys) {
		this.keyList = new ArrayList<String>();
		this.keyList.add(key);
		
		//in case the String... is empty, you can't do keyList.addAll(Arrays.asList(keys))
		for(String k : keys){
			this.keyList.add(k);
		}
	}
	
	public List<String> getKeyList() {
		return keyList;
	}
}
