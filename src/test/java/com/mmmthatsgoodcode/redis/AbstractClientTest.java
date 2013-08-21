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
import com.google.common.hash.Hashing;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.mmmthatsgoodcode.redis.client.NoConnectionsAvailableException;
import com.mmmthatsgoodcode.redis.client.Transaction;
import com.mmmthatsgoodcode.redis.client.monitor.LoggingMonitor;
import com.mmmthatsgoodcode.redis.protocol.PendingResponse;
import com.mmmthatsgoodcode.redis.protocol.AbstractResponse;
import com.mmmthatsgoodcode.redis.protocol.Response;
import com.mmmthatsgoodcode.redis.protocol.request.Exists;
import com.mmmthatsgoodcode.redis.protocol.request.Get;
import com.mmmthatsgoodcode.redis.protocol.request.Ping;
import com.mmmthatsgoodcode.redis.protocol.request.Set;
import com.mmmthatsgoodcode.redis.protocol.request.Setex;
import com.mmmthatsgoodcode.redis.protocol.request.Watch;
import com.mmmthatsgoodcode.redis.util.RedisClientMurmurHash;

public abstract class AbstractClientTest {
	
	protected static Client CLIENT;
	
	
	@Test
	public void testSimpleCommands() throws InterruptedException, NoConnectionsAvailableException {
		
		CLIENT.send(new Ping());
				
		
	}
	
	@Test
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
					
//					System.out.println( "getting "+id);
					Context timer = null;
					try {
						String setResponse = CLIENT.send(new Set(id, value)).get().value();

//						System.out.println( id+" value - "+CLIENT.send(new Get(id)).get(1, TimeUnit.SECONDS).value() );
						timer = getLatency.time();
						String response = (String) CLIENT.send(new Get(id)).get(100, TimeUnit.MILLISECONDS).value();
						timer.stop();
						assertTrue(response.equals(value));
						
					} catch (IllegalStateException | InterruptedException
							 | TimeoutException e) {
						System.err.println(id+" Timed out");
						if (timer != null) timer.stop();
					} catch (NoConnectionsAvailableException e) {
						System.err.println(id+" No conn available");
					}
					
//					responses.add( CLIENT.send(new Get(id)) );

				}
				
			});
		}
		
		
		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.HOURS);
		
		System.out.println("Multi-threaded set/get");
		System.out.println("Total "+getLatency.getCount()+"Set+Get, 99th:"+getLatency.getSnapshot().get99thPercentile()/1000000+"ms");

		
	}
	
	
	@Test
	public void singleThreadedPipelineTest() throws InterruptedException, NoConnectionsAvailableException, ExecutionException, TimeoutException {
		
		final Timer getLatency = new Timer();
		final List<AbstractResponse> responses = new ArrayList<AbstractResponse>();

		for (int r=1; r <= 1000; r++) {

			final String id = UUID.randomUUID().toString();
			responses.add( CLIENT.send(new Setex(id, "i'm really really random", 5000), new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						responses.add( CLIENT.send(new Get(id)).get(100, TimeUnit.MILLISECONDS) );
					} catch (NoConnectionsAvailableException | InterruptedException | TimeoutException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}).get(1000, TimeUnit.MILLISECONDS) );
//			responses.add( CLIENT.send(new Set(id, "i'm really really random")) );


		}		
		
		
		for(Response response:responses) {
				System.out.println(response);
		}
				
	}
	
	@Test
	public void testTransactions() throws InterruptedException, ExecutionException, TimeoutException {
		
		final Timer getLatency = new Timer();
		
		for (int r=1; r <= 1000; r++) {

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
					 | TimeoutException | NoConnectionsAvailableException e) {
				System.err.println(id+" Timed out");
				if (timer != null) timer.stop();
			}
		}
		
		System.out.println("Single threaded transactions");
		System.out.println("Total "+getLatency.getCount()+" Set+Get Transactions, 99th:"+getLatency.getSnapshot().get99thPercentile()/1000000+"ms");

	}
	
	@Test
	public void testMultiThreadedTransactions() throws InterruptedException {
		
		ExecutorService executor = Executors.newFixedThreadPool(1);
		final Timer getLatency = new Timer();
		
		for (int r=1; r <= 10; r++) {
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
						List<Response> responses = CLIENT.send(
								new Transaction()
								.pin(CLIENT.hostForKey(id))
								.add(new Set(id, value), new Get(id))
								).get(5, TimeUnit.SECONDS).value();
						timer.stop();
						assertEquals(responses.size(), 2);
						assertEquals((String) responses.get(1).value(), value);
						
					} catch (IllegalStateException | InterruptedException
							 | TimeoutException | NoConnectionsAvailableException e) {
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
