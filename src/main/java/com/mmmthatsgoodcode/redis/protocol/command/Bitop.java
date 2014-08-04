package com.mmmthatsgoodcode.redis.protocol.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Bitop extends AbstractCommand<IntegerReply>{
	
	private final byte[] destinationKey;
	private final List<byte[]> keys;
	private final byte[] operation;
	//TODO maybe make a map of the operations, and check if they are in the list(AND, OR, XOR, NOT)
	// or trust that what we are given is correct...
	
	public Bitop(byte[] operation, byte[] destinationKey, byte[] key1) {
		this.destinationKey = destinationKey;
		this.keys = new ArrayList<byte[]>();
		this.keys.add(key1);
		this.operation = operation;
	}
	
	public Bitop(byte[] operation, byte[] destinationKey, byte[] key1, byte[]... keys) {
		this.destinationKey = destinationKey;
		this.keys = Arrays.asList(keys);
		this.keys.add(key1);
		this.operation = operation;
	}
	
	public byte[] getDestinationKey() {
		return destinationKey;
	}
	
	public List<byte[]> getKeys() {
		return keys;
	}
	
	public byte[] getOperation() {
		return operation;
	}
}
