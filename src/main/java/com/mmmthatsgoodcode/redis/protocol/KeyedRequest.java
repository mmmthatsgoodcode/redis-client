package com.mmmthatsgoodcode.redis.protocol;

public abstract class KeyedRequest<T extends Response> extends Request<T> {

	protected final String key;
	
	public KeyedRequest(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
	

}
