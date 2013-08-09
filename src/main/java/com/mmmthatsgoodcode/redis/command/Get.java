package com.mmmthatsgoodcode.redis.command;

import com.mmmthatsgoodcode.redis.Command;

public class Get extends Command {

	private static final String COMMAND = "GET";
	
	public Get(String key) {
		addParamter(key);
	}
	
	@Override
	public String getName() {
		return COMMAND;
	}

	
	
}
