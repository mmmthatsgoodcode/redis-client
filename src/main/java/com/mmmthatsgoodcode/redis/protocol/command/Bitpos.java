package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.Redis2TextProtocol;
import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Bitpos extends KeyedCommand<IntegerReply>{

	private final byte[] startingBit;
	private final byte[] endingBit;
	private final byte[] bitSearched;
	
	public Bitpos(String key, byte[] bitSearched) {
		super(key);
		this.bitSearched = bitSearched;
		this.startingBit = "0".getBytes(Redis2TextProtocol.ENCODING);
		this.endingBit = "-1".getBytes(Redis2TextProtocol.ENCODING);
	}
	
	public Bitpos(String key, byte[] bitSearched, byte[] startingBit) {
		super(key);
		this.bitSearched = bitSearched;
		this.startingBit = startingBit;
		this.endingBit = "-1".getBytes(Redis2TextProtocol.ENCODING);
	}
	
	public Bitpos(String key, byte[] bitSearched, byte[] startingBit, byte[] endingBit) {
		super(key);
		this.bitSearched = bitSearched;
		this.startingBit = startingBit;
		this.endingBit = endingBit;
	}
	
	public byte[] getBitSearched() {
		return bitSearched;
	}
	
	public byte[] getStartingBit() {
		return startingBit;
	}
	
	public byte[] getEndingBit() {
		return endingBit;
	}
}
