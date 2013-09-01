package com.mmmthatsgoodcode.redis.protocol;

public abstract class KeyedCommand<T extends Reply> extends AbstractCommand<T> {

	protected final String key;
	
	public KeyedCommand(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
	

}
