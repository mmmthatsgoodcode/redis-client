package com.mmmthatsgoodcode.redis.protocol.model;

import java.util.Arrays;
import java.util.List;

import com.mmmthatsgoodcode.redis.protocol.Reply;

public abstract class MultiKeyedCommand<T extends Reply> extends AbstractCommand<T>{

	protected final List<String> keys;
	
	public MultiKeyedCommand(List<String> keys) {
		this.keys = keys;
	}
	
	public MultiKeyedCommand(String key) {
		this(Arrays.asList(key));
	}
	
	public List<String> getKeys() {
		return keys;
	}
}
