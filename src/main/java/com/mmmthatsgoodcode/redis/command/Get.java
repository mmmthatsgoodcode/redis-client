package com.mmmthatsgoodcode.redis.command;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import com.mmmthatsgoodcode.redis.Command;
import com.mmmthatsgoodcode.redis.Response;
import com.mmmthatsgoodcode.redis.Command.Decoder;

public class Get extends Command {

	private static final String COMMAND = "GET";
	
	public Get(String key) {
		addParamter(key);
	}
	
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
				
				return responses;
				
				
			}
			
		};
		
	}

	
	
}
