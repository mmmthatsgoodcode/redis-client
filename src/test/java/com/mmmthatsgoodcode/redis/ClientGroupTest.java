package com.mmmthatsgoodcode.redis;

import org.junit.Test;

import com.mmmthatsgoodcode.redis.client.monitor.LoggingMonitor;

public class ClientGroupTest {

	@Test
	public void testClientGroup() throws InterruptedException {
		
		ClientGroup clientGroup = new ClientGroup.Builder().addServer("localhost", 6379).addMonitor(new LoggingMonitor()).build();
		clientGroup.connect();
		
		Thread.sleep(10000);
		
	}
	
}
