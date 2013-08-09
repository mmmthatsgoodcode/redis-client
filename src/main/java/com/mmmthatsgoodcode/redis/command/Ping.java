package com.mmmthatsgoodcode.redis.command;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import com.mmmthatsgoodcode.redis.Command;
import com.mmmthatsgoodcode.redis.Response;

public class Ping extends Command {

	private static final String COMMAND = "PING";
	
	@Override
	public String getName() {
		return COMMAND;
	}

	@Override
	public Decoder getDecoder() {
		
		return new Decoder() {

			@Override
			public List<Response> decode(ByteBuf buf) {
				
				List<Response> responses = new ArrayList<Response>();
				
				System.out.println();
				
				return responses;
				
				
			}
			
		};
		
	}

}
