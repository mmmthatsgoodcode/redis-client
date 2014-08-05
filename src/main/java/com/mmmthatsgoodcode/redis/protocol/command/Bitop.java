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
		List<byte[]> tmp = new ArrayList<byte[]>();
		tmp.add(key1);
		this.keys = tmp;
		this.operation = operation;
	}
	
	public Bitop(byte[] operation, byte[] destinationKey, byte[] key1, byte[]... keys) {
		this.destinationKey = destinationKey;
		
		List<byte[]> tmp = new ArrayList<byte[]>();
		tmp.add(key1);
		for(byte[] key : keys){
			tmp.add(key);
		}
		this.keys = tmp;
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
