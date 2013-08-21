package com.mmmthatsgoodcode.redis.client.monitor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.redis.ClientMonitor;
import com.mmmthatsgoodcode.redis.Connection;
import com.mmmthatsgoodcode.redis.Host;

/**
 * A monitor that will try and re-establish broken Channels
 * @author andras
 *
 */
public class SelfHealingMonitor implements ClientMonitor {

	protected static final Logger LOG = LoggerFactory.getLogger(SelfHealingMonitor.class);
	
	protected class Healer implements Runnable {

		private final int interval;
		private LinkedBlockingQueue<Host> reconnectQueue = new LinkedBlockingQueue<Host>();
		
		public Healer(int interval) {
			this.interval = interval;
		}
		
		@Override
		public void run() {
			
			try {
				
				while(true) {
					Host host = reconnectQueue.take();
					LOG.warn("Trying to create new connection to {}", host);
					host.createConnection().connect();

					Thread.sleep(interval);
					
				}
				
				
				
			} catch (InterruptedException e) {
				
			}
				
		
		}
		
		public void needReconnect(Host host) {
			reconnectQueue.add(host);
		}
		
	}
	
	protected final Healer healer;
	
	public SelfHealingMonitor(int interval) {
		healer = new Healer(interval);
		Thread healerThread = new Thread(healer, "RedisReconnectThread");
		healerThread.start();
	}


	@Override
	public void connectionFailed(Connection connection, Throwable cause) {
		connection.discard(cause);
	}

	@Override
	public void connectionLost(Connection connection, Throwable cause) {
		connection.discard(cause);
	}


	@Override
	public void connectionCreated(Connection client) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void connectionInProgress(Connection client) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void connected(Connection client) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void connectionDiscarded(Connection connection, Throwable cause) {
		LOG.warn("Discarded connection, incrementing connection create count");
		healer.needReconnect(connection.getHost());
		
	}


}
