package com.mmmthatsgoodcode.redis.protocol.command;

public class Brpop extends Blpop{

	public Brpop(byte[] timeout, String key, String... keys) {
		super(timeout, key, keys);
	}	
}
