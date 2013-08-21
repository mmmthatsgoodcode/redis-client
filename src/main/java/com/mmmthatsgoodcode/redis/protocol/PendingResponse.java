package com.mmmthatsgoodcode.redis.protocol;

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

public class PendingResponse<T extends Response> implements Future<T> {

	private static final Logger LOG = LoggerFactory.getLogger(PendingResponse.class);
	
	protected Semaphore lock = new Semaphore(1);
	protected T response = null;
	protected final Request<T> request;
	private RedisClientException exception = null;
	private List<Runnable> onCompleteCallbacks = new ArrayList<Runnable>();
	private List<Runnable> onSentCallbacks = new ArrayList<Runnable>();
	
	private ChannelFuture channelFuture = null;
	
	public PendingResponse(Request<T> request) {
		this.request = request;
		this.lock.acquireUninterruptibly();
		
	}
	
	public void onSent(Runnable...onSent) {
		this.onSentCallbacks.addAll(onSentCallbacks);
	}
	
	public void onComplete(Runnable...onComplete) {
		this.onCompleteCallbacks.addAll(Arrays.asList(onComplete));
	}
	
	public final void sent(ChannelFuture channelFuture) {
		this.channelFuture = channelFuture;
		for (Runnable onSent:onSentCallbacks) {
			onSent.run();
		}
	}
	
	/**
	 * Notify Request of the received Response, release semaphore
	 * @param response
	 */
	public final void finalize(T response) {
		LOG.debug("Finalized {}", this);
		this.response = response;
		this.request.responseReceived(this.response);
		for (Runnable onComplete:onCompleteCallbacks) {
			onComplete.run();
		}
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
			return response;
		} catch (InterruptedException e) {
			return null;
		}
	}
	
	public T getOrCatch() throws RedisClientException {
		LOG.debug("Now waiting on {} or an exception", this);

		try {
			this.lock.acquire();
			if (getException() != null) throw getException();
			return response;
		} catch (InterruptedException e) {
			return null;
		}	
	}

	@Override
	public T get(long timeout, TimeUnit unit)
			throws InterruptedException, TimeoutException {
		LOG.debug("Now waiting on {}", this);

		if (this.lock.tryAcquire(timeout, unit) == true) {
			return response;
		}
		throw new TimeoutException();
	}
	
	public T getOrCatch(long timeout, TimeUnit unit) throws RedisClientException, InterruptedException, TimeoutException {
		LOG.debug("Now waiting on {} or an exception", this);

		LOG.debug("Now waiting on {}", this);

		if (this.lock.tryAcquire(timeout, unit) == true) {
			if (getException() != null) throw getException();
			return response;
		}
		
		throw new TimeoutException();
	}

	
	public Request<T> getRequest() {
		return request;
	}

	public RedisClientException getException() {
		return exception;
	}
	
	public ChannelFuture getChannelFuture() {
		return channelFuture;
	}

}
