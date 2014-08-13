package com.mmmthatsgoodcode.redis;

import org.junit.BeforeClass;

import com.google.common.hash.Hashing;
import com.mmmthatsgoodcode.redis.client.monitor.LoggingMonitor;

public class ClientTest extends AbstractClientTest {

	@BeforeClass
	public static void createClient() {
		
		CLIENT = new RedisClient.Builder()
		.addHost("127.0.0.1", 6379)
		.addHost("127.0.0.1", 6380)
		.addHost("127.0.0.1", 6381)
		.addMonitor(new LoggingMonitor())
		.withTrafficLogging(true)
		.shouldHash(true)
		.withHashFunction(Hashing.murmur3_128())
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
	
}
