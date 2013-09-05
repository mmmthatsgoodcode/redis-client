package com.mmmthatsgoodcode.redis.protocol.command;

public class Setnx extends Set {

	private final byte[] NAME = "SETNX".getBytes(ENCODING);
	
	public Setnx(String key, String value) {
		super(key, value);
	}
	
	@Override
	public byte[] getName() {
		return NAME;
	}

}
