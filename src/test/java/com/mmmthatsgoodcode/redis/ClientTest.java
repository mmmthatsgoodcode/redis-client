package com.mmmthatsgoodcode.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.mmmthatsgoodcode.redis.client.NoConnectionsAvailableException;
import com.mmmthatsgoodcode.redis.client.monitor.LoggingMonitor;
import com.mmmthatsgoodcode.redis.protocol.PendingResponse;
import com.mmmthatsgoodcode.redis.protocol.Response;
import com.mmmthatsgoodcode.redis.protocol.request.Get;
import com.mmmthatsgoodcode.redis.protocol.request.Ping;
import com.mmmthatsgoodcode.redis.protocol.request.Set;

public class ClientTest {
	
	private static Client CLIENT;
	
	@BeforeClass
	public static void createClient() {
		
		CLIENT = new Client.Builder()
		.addHost("127.0.0.1", 6379)
		.addHost("127.0.0.1", 6380)
		.addMonitor(new LoggingMonitor())
		.shouldBatch(false)
		.shouldHash(true)
		.withConnectionsPerHost(1)
		.build();
		
		CLIENT.connect();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	
	@Test
	public void testSimpleCommands() throws InterruptedException, NoConnectionsAvailableException {
		
		CLIENT.send(new Ping());
				
		
	}
//	
//	@Test
//	public void memoryLeakTest() {
//		
//		for (int r=1; r <= 1000000; r++) {
//			
//			String id = UUID.randomUUID().toString();
//			CLIENT.send(new Set(id, "value-for-"+id)).get();
//			CLIENT.send(new Get(id)).get();
//			
//		}
//		
//	}
//	
	
	@Test
	public void multiThreadedPipelineTest() throws InterruptedException {
				
		ExecutorService executor = Executors.newFixedThreadPool(8);
		final Timer getLatency = new Timer();
		
		for (int r=1; r <= 100000; r++) {
			executor.execute(new Runnable() {

				@Override
				public void run() {
					String id = UUID.randomUUID().toString();
					Context timer = null;
					try {
						
						String setResponse = (String) CLIENT.send(new Set(id, "value-for-"+id)).get().value();

						timer = getLatency.time();
						String response = (String) CLIENT.send(new Get(id)).get(100, TimeUnit.MILLISECONDS).value();
						timer.stop();
						if (response == null) System.err.println(id+" not found");
//						else System.out.println(id+"="+response);
						
					} catch (IllegalStateException | InterruptedException
							| ExecutionException | TimeoutException e) {
						System.err.println(id+" Timed out");
					} catch (NoConnectionsAvailableException e) {
						System.err.println(id+" Failed to send");
					} finally {
						if (timer != null) timer.stop();
					}
					
//					responses.add( CLIENT.send(new Get(id)) );

				}
				
			});
		}
		
		
		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.HOURS);
		
		System.out.println("Total "+getLatency.getCount()+"Gets, 99th:"+getLatency.getSnapshot().get99thPercentile()/1000000+"ms");

		
	}
	
	
	@Test
	@Ignore
	public void singleThreadedPipelineTest() throws InterruptedException, NoConnectionsAvailableException {
		
		final Timer getLatency = new Timer();
		final List<PendingResponse> responses = new ArrayList<PendingResponse>();

		for (int r=1; r <= 10; r++) {

			String id = UUID.randomUUID().toString();
			responses.add( CLIENT.send(new Set(id, "i'm really really random")) );
			responses.add( CLIENT.send(new Get(id)) );

		}		
		
		
		for(PendingResponse response:responses) {
			try {
				response.get(100, TimeUnit.MILLISECONDS);
			} catch (ExecutionException | TimeoutException e) {
				System.err.println("Timed out");
			}
		}
				
	}
	
}
