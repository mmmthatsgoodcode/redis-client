package com.mmmthatsgoodcode.redis.command;

import com.mmmthatsgoodcode.redis.Command;

public class Ping extends Command {

	private static final String COMMAND = "PING";
	
	@Override
	public String getName() {
		return COMMAND;
	}

}
