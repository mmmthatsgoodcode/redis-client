package com.mmmthatsgoodcode.redis.protocol;

import com.mmmthatsgoodcode.redis.protocol.model.PendingReply;

import io.netty.buffer.ByteBuf;

/**
 * Holds a PendingReply (a Future) and provides a way to update it once the Redis Reply has been received for this Command.
 * @author andras
 *
 * @param <T>
 */
public interface Command<T extends Reply> {


	/**
	 * Called by PendingReply.fulfill() before the semaphore is returned.
	 * Allows for the Command to perform processing on the Reply before it is made available to any client
	 * waiting on PendingReply.get()
	 */
	public abstract void replyReceived(T reply);
	public abstract String getName();
	public abstract int getArgc();

	public abstract void setArgc(int argc);

	public abstract PendingReply<T> getReply();

}