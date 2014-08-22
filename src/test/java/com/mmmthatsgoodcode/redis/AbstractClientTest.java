package com.mmmthatsgoodcode.redis;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;
import com.mmmthatsgoodcode.redis.client.NoConnectionsAvailableException;
import com.mmmthatsgoodcode.redis.client.RedisClientException;
import com.mmmthatsgoodcode.redis.client.Transaction;
import com.mmmthatsgoodcode.redis.protocol.Reply;
import com.mmmthatsgoodcode.redis.protocol.command.Exists;
import com.mmmthatsgoodcode.redis.protocol.command.Get;
import com.mmmthatsgoodcode.redis.protocol.command.MSet;
import com.mmmthatsgoodcode.redis.protocol.command.Ping;
import com.mmmthatsgoodcode.redis.protocol.command.SAdd;
import com.mmmthatsgoodcode.redis.protocol.command.SInter;
import com.mmmthatsgoodcode.redis.protocol.command.SUnion;
import com.mmmthatsgoodcode.redis.protocol.command.Set;
import com.mmmthatsgoodcode.redis.protocol.command.Watch;
import com.mmmthatsgoodcode.redis.protocol.model.AbstractReply;

public abstract class AbstractClientTest {
	
	protected static RedisClient CLIENT;
	protected final Logger LOG = LoggerFactory.getLogger(AbstractClientTest.class);
	
	
	@Test
	//@Ignore
	public void MultiplexingSInter() throws InterruptedException{
		//Generating random Sets
				Map<String, List<byte[]>> keysvalues = new HashMap<String, List<byte[]>>();
				List<byte[]> availableMembers = new ArrayList<byte[]>();
				
				
				for(int r=1; r <=9;r++){
					String key = UUID.randomUUID().toString();
					keysvalues.put(key, new ArrayList<byte[]>());
				}
				
				for(int i=0 ; i<3; i++){
					availableMembers.add(("member"+i).getBytes());
				}
				
				Random random = new Random();
				
				for(Entry<String, List<byte[]>> entry : keysvalues.entrySet()){
					for(int i=0 ; i<5 ; i++){
						entry.getValue().add(availableMembers.get(random.nextInt(availableMembers.size())));
					}
				}
				
				try{
					//Adding each set to DB
					LOG.debug("SADD : ");
					for(Entry<String, List<byte[]>> entry : keysvalues.entrySet()){
						CLIENT.send(new SAdd(entry.getKey(), entry.getValue()));
					}

					LOG.debug("SINTER : ");
					List<Reply> replies = CLIENT.send( new SInter( new ArrayList<String>( keysvalues.keySet() ) ) ).get().value();
					LOG.debug("\n\nReceived Replies\n");
					for(Reply re : replies){
						LOG.debug("{}",new String((byte[])re.value()));
					}
					
					boolean isthere = false;
					LOG.debug("\n\n\nStarting Check");
					if(replies.size()>0){
						//checks if each reply is unique
						for(Entry<String, List<byte[]>> entry : keysvalues.entrySet()){
							isthere = false;
							for(Reply rep : replies){
								for(byte[] bytes : entry.getValue()){
									if(new String(bytes).equals(new String((byte[])rep.value()))){
										isthere=true;
									}
								}
							}
							if(!isthere){
								fail();
							}
						}
					}else{
					LOG.debug("Empty set of Reply");
					}
					LOG.debug("All went well!");
				}catch(NoConnectionsAvailableException e){
					LOG.error("No Connection available");
				}
	}
	
	@Test
	//@Ignore
	public void MultiplexingSUnion() throws InterruptedException{
		
		//Generating random Sets
		Map<String, List<byte[]>> keysvalues = new HashMap<String, List<byte[]>>();
		List<byte[]> availableMembers = new ArrayList<byte[]>();
		
		
		for(int r=1; r <=9;r++){
			String key = UUID.randomUUID().toString();
			keysvalues.put(key, new ArrayList<byte[]>());
		}
		
		for(int i=0 ; i<50; i++){
			availableMembers.add(("member"+i).getBytes());
		}
		
		Random random = new Random();
		
		for(Entry<String, List<byte[]>> entry : keysvalues.entrySet()){
			for(int i=0 ; i<5 ; i++){
				entry.getValue().add(availableMembers.get(random.nextInt(availableMembers.size())));
			}
		}
		
		try{
			//Adding each set to DB
			LOG.debug("SADD : ");
			for(Entry<String, List<byte[]>> entry : keysvalues.entrySet()){
				CLIENT.send(new SAdd(entry.getKey(), entry.getValue()));
			}

			LOG.debug("SUNION : ");
			List<Reply> replies = CLIENT.send( new SUnion( new ArrayList<String>( keysvalues.keySet() ) ) ).get().value();
			
			List<Reply> comparator = new ArrayList<Reply>();
			
			//checks if each reply is unique
			for(Reply reply : replies){
				if(comparator.contains(reply)){
					fail();
				}
				else{
					comparator.add(reply);
				}
			}
		}catch(NoConnectionsAvailableException e){
			LOG.error("No Connection available");
		}
		
	}
	
	
	@Test
	//@Ignore
	public void MultiplexingMSet() throws InterruptedException{
		
		LinkedHashMap<String, byte[]> keysvalues = new LinkedHashMap<String, byte[]>();
		boolean allgood = true;
		
		for(int r=1; r <=9;r++){
			String key = UUID.randomUUID().toString();
			byte[] value = ("value-for-"+key).getBytes();
			keysvalues.put(key, value);
		}
		
		try {
			LOG.debug("MSET : ");
			
			//MSET()
			CLIENT.send(new MSet(keysvalues));
			LOG.debug("send(MSet) done");
			
			LOG.debug("\n\n\nMap size = "+keysvalues.size()+"\n\n\n");
			
			
			LOG.debug("GET : ");
			for(Entry<String, byte[]> entry : keysvalues.entrySet()){
				byte[] value = CLIENT.send(new Get(entry.getKey())).get().value();
				System.out.println("response = "+new String(value));
				System.out.println("original = "+new String(entry.getValue()));
				
				if(!new String(value).equals(new String(entry.getValue()))){
					LOG.error("values don't match!");
					allgood = false;
				}
			}
			Thread.sleep(2000);
			assertEquals(allgood, true);
		} catch (NoConnectionsAvailableException e) {
			LOG.error("No Connection available");
		}
	}
	
	
	@Test
	@Ignore
	public void testSimpleCommands() throws InterruptedException, NoConnectionsAvailableException {
		
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
//					System.out.println( "set reply - "+CLIENT.send(new Set(id, "value-for-"+id)).get().value() );
					String value = "value-for-"+id;
					
//					System.out.println( "getting "+id);
					Context timer = null;
					try {
						String setreply = CLIENT.send(new Set(id, value.getBytes())).get().value();

//						System.out.println( id+" value - "+CLIENT.send(new Get(id)).get(1, TimeUnit.SECONDS).value() );
						timer = getLatency.time();
						String reply = new String( CLIENT.send(new Get(id)).get().value() );
						timer.stop();
						assertEquals(value, reply);
						
					} catch (IllegalStateException e) {
						System.err.println(id+" Timed out");
						if (timer != null) timer.stop();
					} catch (NoConnectionsAvailableException e) {
						System.err.println(id+" No conn available");
					}
					
//					replies.add( CLIENT.send(new Get(id)) );

				}
				
			});
		}
		
		
		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.HOURS);
		
		System.out.println("Multi-threaded set/get");
		System.out.println("Total "+getLatency.getCount()+"Set+Get, 99th:"+getLatency.getSnapshot().get99thPercentile()/1000000+"ms");

		
	}
	
	
	@Test
	@Ignore
	public void singleThreadedPipelineTest() throws InterruptedException, NoConnectionsAvailableException, ExecutionException, TimeoutException {
		
		final Timer getLatency = new Timer();
		final List<AbstractReply> replies = new ArrayList<AbstractReply>();

		for (int r=1; r <= 1000; r++) {

			final String id = UUID.randomUUID().toString();
			replies.add( CLIENT.send(new Set(id, ("value-"+id).getBytes())).get() );
			
					try {
						replies.add( CLIENT.send(new Exists(id)).get() );
					} catch (NoConnectionsAvailableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

//			replies.add( CLIENT.send(new Set(id, "i'm really really random")) );


		}		
		
		
		for(Reply reply:replies) {
				System.out.println(reply);
		}
				
	}
	
	@Test
	@Ignore
	public void testTransactions() throws InterruptedException, ExecutionException, TimeoutException {
		
		final Timer getLatency = new Timer();
		
		for (int r=1; r <= 10000; r++) {

			String id = UUID.randomUUID().toString();
			byte[] value = ("value-for-"+id).getBytes();
			Context timer = null;
			
			try {
				timer = getLatency.time();
				List<Reply> replies = CLIENT.send(new Transaction(new Watch(id)).add(new Set(id, value), new Exists(id), new Get(id))).get(100, TimeUnit.MILLISECONDS).value();
				timer.stop();
//				System.out.println(replies);

				assertEquals(replies.size(), 3);
				assertEquals(new String((byte[]) replies.get(2).value()), new String(value));
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
	@Ignore
	public void testMultiThreadedTransactions() throws InterruptedException {
		
		ExecutorService executor = Executors.newFixedThreadPool(4);
		final Timer getLatency = new Timer();
		
		for (int r=1; r <= 1000; r++) {
			executor.execute(new Runnable() {

				@Override
				public void run() {
					String id = UUID.randomUUID().toString();
//					System.out.println( "setting "+id);
//					System.out.println( "set reply - "+CLIENT.send(new Set(id, "value-for-"+id)).get().value() );
					byte[] value = ("value-for-"+id).getBytes();
					
//					System.out.println( "getting "+id);
					Context timer = null;
					try {
//						System.out.println( id+" value - "+CLIENT.send(new Get(id)).get(1, TimeUnit.SECONDS).value() );
						timer = getLatency.time();
						List<Reply> replies = CLIENT.send(
								new Transaction()
								.pin(CLIENT.hostForKey(id))
								.add(new Set(id, value), new Get(id))
								).getOrCatch(10, TimeUnit.MILLISECONDS).value();
						timer.stop();
						assertEquals(replies.size(), 2);
						assertEquals(new String((byte[])replies.get(1).value()), new String(value));
						
					} catch (IllegalStateException | InterruptedException
							 | TimeoutException | NoConnectionsAvailableException e) {
						System.err.println(id+" Timed out");
						if (timer != null) timer.stop();
					} catch (RedisClientException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.exit(1);
					}
					
//					replies.add( CLIENT.send(new Get(id)) );

				}
				
			});
		}
		
		
		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.HOURS);
		
		System.out.println("Multi-threaded transactions");
		System.out.println("Total "+getLatency.getCount()+" Set+Get Transactions, median:"+getLatency.getSnapshot().getMedian()/1000000+"ms 98th:"+getLatency.getSnapshot().get98thPercentile()/1000000+"ms 99th:"+getLatency.getSnapshot().get99thPercentile()/1000000+"ms");
		
	}
	
}
