package com.mmmthatsgoodcode.redis.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PendingResponse implements Future {

	private Semaphore lock = new Semaphore(1);
	private Response response = null;
	
	public PendingResponse() {
		this.lock.acquireUninterruptibly();
		
	}
	
	public void finalize(Response response) {
		this.response = response;
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
	public Response get() {
		try {
			this.lock.acquire();
			return response;
		} catch (InterruptedException e) {
			return null;
		}
	}

	@Override
	public Response get(long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		if (this.lock.tryAcquire(timeout, unit) == true) return response;
		throw new TimeoutException();
	}

}
