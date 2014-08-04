package com.mmmthatsgoodcode.redis.protocol.command;

public class Brpop extends Blpop{

	public Brpop(byte[] timeout, String key, byte[]... keys) {
		super(timeout, key, keys);
	}	
}
