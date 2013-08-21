package com.mmmthatsgoodcode.redis;

import org.junit.BeforeClass;

import com.google.common.hash.Hashing;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.mmmthatsgoodcode.redis.client.monitor.LoggingMonitor;

public class DisruptorClientTest extends AbstractClientTest {

	@BeforeClass
	public static void createClient() {
		
		CLIENT = new DisruptorClient.Builder()
		.withProcessingBufferSize(1024)
		.withProcessingWaitStrategy(new BlockingWaitStrategy())
		.addHost("127.0.0.1", 6379)
		.addHost("127.0.0.1", 6380)
		.addMonitor(new LoggingMonitor())
		.withTrafficLogging(true)
		.shouldHash(true)
		.withHashFunction(Hashing.murmur3_128())
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
	
}

