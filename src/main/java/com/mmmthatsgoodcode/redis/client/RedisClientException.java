package com.mmmthatsgoodcode.redis.client;

import com.mmmthatsgoodcode.redis.protocol.Request;
import com.mmmthatsgoodcode.redis.protocol.Response;

public class RedisClientException extends Exception {

	public RedisClientException() {
		super();
	}
	
	public RedisClientException(Throwable cause) {
		super(cause);
	}


}
