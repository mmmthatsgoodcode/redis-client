package com.mmmthatsgoodcode.redis;

import org.junit.Before;
import org.junit.Test;

public class ClientTest {

	protected Client client = new Client();
	
	@Before
	public void connect() {
		client.connect("localhost", 6379).syncUninterruptibly();
	}
	
	@Test
	public void testConnect() throws InterruptedException {
		
		Thread.sleep(10000);
		
	}
	
}
