package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Linsert extends KeyedCommand<IntegerReply> {

	// TODO possibility to force the position to be either BEFORE or AFTER by checking, and if it is neither, assign AFTER automatically
	private final byte[] position;
	private final byte[] pivot;
	private final byte[] value;
	
	public Linsert(String key, byte[] position, byte[] pivot, byte[] value) {
		super(key);
		this.position = position;
		this.pivot = pivot;
		this.value = value;
	}

	public byte[] getPosition() {
		return position;
	}
	
	public byte[] getPivot() {
		return pivot;
	}
	
	public byte[] getValue() {
		return value;
	}
}
