package com.mmmthatsgoodcode.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import org.junit.Before;
import org.junit.Test;

import com.mmmthatsgoodcode.redis.command.Get;
import com.mmmthatsgoodcode.redis.command.Ping;

public class ClientTest {

	protected Client client = new Client();

	
	@Test
	public void testConnect() throws InterruptedException, ExecutionException {
		
		client.connect("localhost", 6379);

		for (Response response:client.send(new Get("foo"))) {
			System.out.println(response);
		}
		
		

	}
	
	
	private void multiThreadPing(final Client client) throws InterruptedException, ExecutionException {
		
		ExecutorService executor = Executors.newFixedThreadPool(64);
		
		final List<ResponseContainer> responses = new ArrayList<ResponseContainer>();
		
		for(int c=1;c<=10000;c++) {
			
			final Ping command = new Ping();
			executor.submit(new Runnable() {
	
				@Override
				public void run() {
					client.send(command);
				}
				
			});
			
			responses.add( command.getResponse() );
		
		}
		
		for (ResponseContainer response:responses) {
			System.out.println(response.get());

		}
		
		
	}
	
}
