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
import static org.junit.Assert.*;

import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.mmmthatsgoodcode.redis.client.Transaction;
import com.mmmthatsgoodcode.redis.client.monitor.LoggingMonitor;
import com.mmmthatsgoodcode.redis.protocol.PendingResponse;
import com.mmmthatsgoodcode.redis.protocol.Response;
import com.mmmthatsgoodcode.redis.protocol.request.Exists;
import com.mmmthatsgoodcode.redis.protocol.request.Get;
import com.mmmthatsgoodcode.redis.protocol.request.Ping;
import com.mmmthatsgoodcode.redis.protocol.request.Set;
import com.mmmthatsgoodcode.redis.protocol.request.Setex;
import com.mmmthatsgoodcode.redis.protocol.request.Watch;
import com.mmmthatsgoodcode.redis.util.RedisClientMurmurHash;

public class ClientTest {
	
	private static Client CLIENT;
	
	@BeforeClass
	public static void createClient() {
		
		CLIENT = new Client.Builder()
		.addHost("127.0.0.1", 6379)
		.addHost("127.0.0.1", 6380)
		.addMonitor(new LoggingMonitor())
		.withTrafficLogging(true)
		.shouldHash(true)
		.withHashFunction(new RedisClientMurmurHash())
		.withProcessingBufferSize(1024)
		.withProcessingWaitStrategy(new BlockingWaitStrategy())
		.withConnectionsPerHost(4)
		.withTrafficLogging(true)
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
	@Ignore
	public void testSimpleCommands() throws InterruptedException {
		
		CLIENT.send(new Ping());
				
		
	}
	
	@Test
	@Ignore
	public void multiThreadedPipelineTest() throws InterruptedException {
				
		ExecutorService executor = Executors.newFixedThreadPool(8);
		final Timer getLatency = new Timer();
		
		for (int r=1; r <= 100000; r++) {
			executor.execute(new Runnable() {

				@Override
				public void run() {
					String id = UUID.randomUUID().toString();
//					System.out.println( "setting "+id);
//					System.out.println( "set response - "+CLIENT.send(new Set(id, "value-for-"+id)).get().value() );
					String value = "value-for-"+id;
					String setResponse = CLIENT.send(new Set(id, value)).get().value();
					
//					System.out.println( "getting "+id);
					Context timer = null;
					try {
//						System.out.println( id+" value - "+CLIENT.send(new Get(id)).get(1, TimeUnit.SECONDS).value() );
						timer = getLatency.time();
						String response = (String) CLIENT.send(new Get(id)).get(100, TimeUnit.MILLISECONDS).value();
						timer.stop();
						assertTrue(response.equals(value));
						
					} catch (IllegalStateException | InterruptedException
							| ExecutionException | TimeoutException e) {
						System.err.println(id+" Timed out");
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
	public void singleThreadedPipelineTest() throws InterruptedException {
		
		final Timer getLatency = new Timer();
		final List<PendingResponse> responses = new ArrayList<PendingResponse>();

		for (int r=1; r <= 10; r++) {

			String id = UUID.randomUUID().toString();
			responses.add( CLIENT.send(new Setex(id, "i'm really really random", 5000)) );
//			responses.add( CLIENT.send(new Set(id, "i'm really really random")) );

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
	
	@Test
	public void testTransactions() throws InterruptedException, ExecutionException, TimeoutException {
		
		final Timer getLatency = new Timer();
		
		for (int r=1; r <= 1; r++) {

			String id = UUID.randomUUID().toString();
			String value = "value-for-"+id;
			Context timer = null;
			
			try {
				timer = getLatency.time();
				List<Response> responses = CLIENT.send(new Transaction(new Watch(id)).add(new Set(id, value), new Exists(id), new Get(id))).get(100, TimeUnit.MILLISECONDS).value();
				timer.stop();
				System.out.println(responses);

				assertEquals(responses.size(), 3);
				assertEquals((String) responses.get(2).value(), value);
			} catch (IllegalStateException | InterruptedException
					| ExecutionException | TimeoutException e) {
				System.err.println(id+" Timed out");
				if (timer != null) timer.stop();
			}
		}
		
		System.out.println("Single threaded transactions");
		System.out.println("Total "+getLatency.getCount()+" Set+Get Transactions, 99th:"+getLatency.getSnapshot().get99thPercentile()/1000000+"ms");

	}
	
	@Test
	@Ignore
	public void testMultiThreadedTransactions() throws InterruptedException {
		
		ExecutorService executor = Executors.newFixedThreadPool(4);
		final Timer getLatency = new Timer();
		
		for (int r=1; r <= 100; r++) {
			executor.execute(new Runnable() {

				@Override
				public void run() {
					String id = UUID.randomUUID().toString();
//					System.out.println( "setting "+id);
//					System.out.println( "set response - "+CLIENT.send(new Set(id, "value-for-"+id)).get().value() );
					String value = "value-for-"+id;
					
//					System.out.println( "getting "+id);
					Context timer = null;
					try {
//						System.out.println( id+" value - "+CLIENT.send(new Get(id)).get(1, TimeUnit.SECONDS).value() );
						timer = getLatency.time();
						List<Response> responses = CLIENT.send(new Transaction().pin(CLIENT.hostForKey(id)).add(new Set(id, value)).add(new Get(id))).get(100, TimeUnit.MILLISECONDS).value();
						timer.stop();
						assertEquals(responses.size(), 2);
						assertEquals((String) responses.get(1).value(), value);
						
					} catch (IllegalStateException | InterruptedException
							| ExecutionException | TimeoutException e) {
						System.err.println(id+" Timed out");
						if (timer != null) timer.stop();
					}
					
//					responses.add( CLIENT.send(new Get(id)) );

				}
				
			});
		}
		
		
		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.HOURS);
		
		System.out.println("Multi-threaded transactions");
		System.out.println("Total "+getLatency.getCount()+" Set+Get Transactions, 99th:"+getLatency.getSnapshot().get99thPercentile()/1000000+"ms");
		
	}
	
}
