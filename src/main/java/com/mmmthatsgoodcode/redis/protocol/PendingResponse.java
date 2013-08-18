package com.mmmthatsgoodcode.redis.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PendingResponse<T extends Response> implements Future<T> {

	protected Semaphore lock = new Semaphore(1);
	protected T response = null;
	protected final Request<T> request;
	
	public PendingResponse(Request<T> request) {
		this.request = request;
		this.lock.acquireUninterruptibly();
		
	}
	
	/**
	 * Notify Request of the received Response, release semaphore
	 * @param response
	 */
	public final void finalize(T response) {
		this.response = response;
		this.request.responseReceived(this.response);

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
		try {
			this.lock.acquire();
			return response;
		} catch (InterruptedException e) {
			return null;
		}
	}

	@Override
	public T get(long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		if (this.lock.tryAcquire(timeout, unit) == true) return response;
		throw new TimeoutException();
	}
	
	public Request<T> getRequest() {
		return request;
	}

}
