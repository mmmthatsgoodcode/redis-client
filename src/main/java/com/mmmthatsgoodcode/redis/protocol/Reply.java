package com.mmmthatsgoodcode.redis.protocol;

import com.mmmthatsgoodcode.redis.client.RedisClientException;

public interface Reply<T> {

	public abstract T value() throws IllegalStateException;

	public abstract void setCommand(Command command);

	public abstract Command getCommand();

}