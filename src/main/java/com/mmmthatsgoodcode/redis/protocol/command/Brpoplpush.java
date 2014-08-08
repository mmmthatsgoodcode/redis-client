package com.mmmthatsgoodcode.redis.protocol.command;

public class Brpoplpush extends Rpoplpush {
	
	private final byte[] timeout;
	
	public Brpoplpush(byte[] source, byte[] destination, byte[] timeout) {
		super(source, destination);
		this.timeout = timeout;
	}
	
	public byte[] getTimeout() {
		return timeout;
	}
}
