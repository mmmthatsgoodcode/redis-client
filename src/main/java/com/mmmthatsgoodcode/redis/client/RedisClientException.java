package com.mmmthatsgoodcode.redis.client;

import com.mmmthatsgoodcode.redis.protocol.Command;
import com.mmmthatsgoodcode.redis.protocol.Reply;

public class RedisClientException extends Exception {

	public RedisClientException() {
		super();
	}
	
	public RedisClientException(Throwable cause) {
		super(cause);
	}


}
