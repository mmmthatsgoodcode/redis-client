package com.mmmthatsgoodcode.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ResponseContainer extends ArrayList<Response> implements Future<ResponseContainer> {

	private Semaphore lock = new Semaphore(1);
	private final Command request;
	
	public ResponseContainer(Command request) {
		this.request = request;
		this.lock.acquireUninterruptibly();
	}
	
	public void fill(List<Response> responses) {
		addAll(responses);
		this.lock.release();
	}
		
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		throw new UnsupportedOperationException("Can't interrupt Redis command");
	}

	@Override
	public boolean isCancelled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDone() {
		return this.lock.availablePermits() > 0;
	}

	@Override
	public ResponseContainer get() throws InterruptedException,
			ExecutionException {
		this.lock.acquire();
		return this;
	}

	@Override
	public ResponseContainer get(long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		if (this.lock.tryAcquire(timeout, unit) == true) return this;
		throw new TimeoutException();
	}

	
	
}
