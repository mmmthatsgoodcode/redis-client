package com.mmmthatsgoodcode.redis;

import static org.junit.Assert.*;

import org.junit.Test;

import com.mmmthatsgoodcode.redis.protocol.request.Get;
import com.mmmthatsgoodcode.redis.protocol.request.Set;

public class ClientTest {

	@Test
	public void testClient() throws InterruptedException {
		
		Client client = new Client().connect("localhost", 6379);
		assertTrue( client.send(new Set("Foo", "Bar")).get().value().equals("OK") );
		assertTrue( client.send(new Get("Foo")).get().value().equals("Bar") );
		
	}
	
}
