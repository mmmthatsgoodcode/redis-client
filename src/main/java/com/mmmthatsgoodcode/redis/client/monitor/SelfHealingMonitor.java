package com.mmmthatsgoodcode.redis.client.monitor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;

import com.mmmthatsgoodcode.redis.ClientMonitor;
import com.mmmthatsgoodcode.redis.Connection;

/**
 * A monitor that will try and re-establish broken Channels
 * @author andras
 *
 */
public class SelfHealingMonitor implements ClientMonitor {

	protected class Healer implements Runnable {

		private final int interval;
		private LinkedBlockingQueue<Connection> reconnectQueue = new LinkedBlockingQueue<Connection>();
		
		public Healer(int interval) {
			this.interval = interval;
		}
		
		@Override
		public void run() {
			
			try {
				
				while(true) {
					Connection connection = reconnectQueue.take();
//					connection.reconnect();
					
					Thread.sleep(interval);
					
				}
				
				
				
			} catch (InterruptedException e) {
				
			}
				
		
		}
		
		public void needReconnect(Connection connection) {
			reconnectQueue.add(connection);
		}
		
	}
	
	protected final Healer healer;
	
	public SelfHealingMonitor(int interval) {
		healer = new Healer(interval);
		Thread healerThread = new Thread(healer, "RedisHealer");
		healerThread.start();
	}


	@Override
	public void connectionFailed(Connection connection, Throwable cause) {
		healer.needReconnect(connection);
	}

	@Override
	public void connectionLost(Connection connection, Throwable cause) {
		healer.needReconnect(connection);
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


}
