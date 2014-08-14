package com.mmmthatsgoodcode.redis.protocol.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;

import com.mmmthatsgoodcode.redis.protocol.Reply;

public abstract class MultiKeyedCommand<T extends Reply> extends AbstractCommand<T>{

	protected final List<String> keys = new ArrayList<String> ();
	
	public MultiKeyedCommand(Map<String, byte[]> keys) {
		for(Entry<String, byte[]> keySet : keys.entrySet()){
			this.keys.add(keySet.getKey());
		}
	}
	
	public List<String> getKeys() {
		return keys;
	}
}
