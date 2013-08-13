package com.mmmthatsgoodcode.redis.client.monitor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;

import com.mmmthatsgoodcode.redis.Client;
import com.mmmthatsgoodcode.redis.ClientMonitor;

/**
 * A monitor that will try and re-establish broken Channels
 * @author andras
 *
 */
public class SelfHealingMonitor implements ClientMonitor {

	protected class Healer implements Runnable {

		private final int interval;
		private LinkedBlockingQueue<Client> reconnectQueue = new LinkedBlockingQueue<Client>();
		
		public Healer(int interval) {
			this.interval = interval;
		}
		
		@Override
		public void run() {
			
			try {
				
				while(true) {
					Client client = reconnectQueue.take();
					client.reconnect();
					
					Thread.sleep(interval);
					
				}
				
				
				
			} catch (InterruptedException e) {
				
			}
				
		
		}
		
		public void needReconnect(Client client) {
			reconnectQueue.add(client);
		}
		
	}
	
	protected final Healer healer;
	
	public SelfHealingMonitor(int interval) {
		healer = new Healer(interval);
		Thread healerThread = new Thread(healer, "RedisHealer");
		healerThread.start();
	}
	
	@Override
	public void clientCreated(Client client) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clientConnecting(Client client) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clientConnectionFailed(Client client, Throwable cause) {
		healer.needReconnect(client);
	}

	@Override
	public void clientDisconnected(Client client, Throwable cause) {
		healer.needReconnect(client);
	}

	@Override
	public void clientConnected(Client client) {
		// TODO Auto-generated method stub
		
	}

}
