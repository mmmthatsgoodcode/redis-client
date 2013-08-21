package com.mmmthatsgoodcode.redis.protocol;

import io.netty.buffer.ByteBuf;

public interface Request<T extends Response> {

	public abstract ByteBuf encode();

	public abstract byte[] getName();

	/**
	 * Called by PendingResponse.fulfill() before the semaphore is returned.
	 * Allows for the Request to perform processing on the Response before it is made available to any client
	 * waiting on PendingResponse.get()
	 */
	public abstract void responseReceived(T response);

	public abstract int getArgc();

	public abstract void setArgc(int argc);

	public abstract PendingResponse<T> getResponse();

}