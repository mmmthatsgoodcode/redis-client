package com.mmmthatsgoodcode.redis.protocol;

import com.mmmthatsgoodcode.redis.client.RedisClientException;

public interface Response<T> {

	public abstract T value() throws IllegalStateException;

	/**
	 * Attempt to decode the Response from the current contents of the inbound buffer.
	 * @return
	 */
	public abstract boolean decode();

	public abstract void setRequest(Request request);

	public abstract Request getRequest();

}