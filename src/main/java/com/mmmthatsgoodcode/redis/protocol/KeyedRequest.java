package com.mmmthatsgoodcode.redis.protocol;

public abstract class KeyedRequest extends Request {

	protected final String key;
	
	public KeyedRequest(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
	

}
