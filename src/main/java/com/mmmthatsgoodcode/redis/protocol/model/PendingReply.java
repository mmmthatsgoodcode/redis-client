package com.mmmthatsgoodcode.redis.protocol.model;

import io.netty.channel.ChannelFuture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.redis.client.RedisClientException;
import com.mmmthatsgoodcode.redis.protocol.Command;
import com.mmmthatsgoodcode.redis.protocol.Reply;

public class PendingReply<T extends Reply> implements Future<T> {

	private static final Logger LOG = LoggerFactory.getLogger(PendingReply.class);
	
	protected Semaphore lock = new Semaphore(1);
	protected T reply = null;
	protected final Command<T> command;
	private RedisClientException exception = null;
	
	private ChannelFuture channelFuture = null;
	
	public PendingReply(Command<T> command) {
		this.command = command;
		this.lock.acquireUninterruptibly();
		
	}
	public final void sent(ChannelFuture channelFuture) {
		this.channelFuture = channelFuture;

	}
	
	/**
	 * Notify the Command of the received Reply, release semaphore
	 * @param reply
	 */
	public final void finalize(T reply) {
		LOG.debug("Finalized {}", this);
		this.reply = reply;
		this.command.replyReceived(this.reply);
		this.lock.release();
	}
	
	public final void finalize(Throwable cause) {
		LOG.debug("Finalized {} with Exception {}", this, cause);
		this.exception = new RedisClientException(exception);
		this.lock.release();
	}
	
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCancelled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public T get() {
		LOG.debug("Now waiting on {}", this);

		try {
			this.lock.acquire();
			return reply;
		} catch (InterruptedException e) {
			return null;
		}
	}
	
	public T getOrCatch() throws RedisClientException {
		LOG.debug("Now waiting on {} or an exception", this);

		try {
			this.lock.acquire();
			if (getException() != null) throw getException();
			return reply;
		} catch (InterruptedException e) {
			return null;
		}	
	}

	@Override
	public T get(long timeout, TimeUnit unit)
			throws InterruptedException, TimeoutException {
		LOG.debug("Now waiting on {}", this);

		if (this.lock.tryAcquire(timeout, unit) == true) {
			return reply;
		}
		throw new TimeoutException();
	}
	
	public T getOrCatch(long timeout, TimeUnit unit) throws RedisClientException, InterruptedException, TimeoutException {
		LOG.debug("Now waiting on {} or an exception", this);

		if (this.lock.tryAcquire(timeout, unit) == true) {
			if (getException() != null) throw getException();
			return reply;
		}
		
		throw new TimeoutException();
	}

	
	public Command<T> getCommand() {
		return command;
	}

	public RedisClientException getException() {
		return exception;
	}
	
	public ChannelFuture getChannelFuture() {
		return channelFuture;
	}

}
