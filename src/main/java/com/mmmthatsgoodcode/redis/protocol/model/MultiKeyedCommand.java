package com.mmmthatsgoodcode.redis.protocol.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;

import com.mmmthatsgoodcode.redis.protocol.Reply;

public abstract class MultiKeyedCommand<T extends Reply> extends AbstractCommand<T>{

	protected final List<String> keys ;
	
	public MultiKeyedCommand(Map<String, byte[]> keys) {
		this.keys = new ArrayList<String>();
		for(Entry<String, byte[]> keySet : keys.entrySet()){
			this.keys.add(keySet.getKey());
		}
	}
	
	public MultiKeyedCommand(List<String> keys) {
		this.keys = keys;
	}
	
	// not really necessary, it just takes the pain away from the user (having to create an ArrayList just for 1 key)
	public MultiKeyedCommand(String key) {
		this.keys = new ArrayList<String>();
		this.keys.add(key);
	}
	
	public List<String> getKeys() {
		return keys;
	}
}
