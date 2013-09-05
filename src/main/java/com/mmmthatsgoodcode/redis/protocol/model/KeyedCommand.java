package com.mmmthatsgoodcode.redis.protocol.model;

import com.mmmthatsgoodcode.redis.protocol.Reply;

public abstract class KeyedCommand<T extends Reply> extends AbstractCommand<T> {

	protected final String key;
	
	public KeyedCommand(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
	

}
