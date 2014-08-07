package com.mmmthatsgoodcode.redis.protocol.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.mmmthatsgoodcode.redis.protocol.Reply;

public abstract class MultiKeyedCommand<T extends Reply> extends AbstractCommand<T>{

	protected final Set<String> keys = new HashSet<String> ();
	
	public MultiKeyedCommand(String key, String... keys) {
		this.keys.add(key);
		this.keys.addAll(Arrays.asList(keys));
	}
	
	public Set<String> getKeys() {
		return keys;
	}
}
