package com.mmmthatsgoodcode.redis.protocol.command;

public class Setex extends Set {

	private final int expiry;
	
	public Setex(String key, byte[] value, int expiry) {
		super(key, value);
		this.expiry = expiry;
	}
	
	public int getExpiry() {
		return expiry;
	}
}
