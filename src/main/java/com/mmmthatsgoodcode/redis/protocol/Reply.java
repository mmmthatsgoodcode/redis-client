package com.mmmthatsgoodcode.redis.protocol;

import com.mmmthatsgoodcode.redis.client.RedisClientException;

public interface Reply<T> {

	public abstract T value() throws IllegalStateException;

	/**
	 * Attempt to decode the Reply from the current contents of the inbound buffer.
	 * @return
	 */
	public abstract boolean decode();

	public abstract void setCommand(Command command);

	public abstract Command getCommand();

}