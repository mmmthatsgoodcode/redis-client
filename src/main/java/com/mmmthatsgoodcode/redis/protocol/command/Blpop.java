package com.mmmthatsgoodcode.redis.protocol.command;

import java.util.Arrays;
import java.util.List;

import com.mmmthatsgoodcode.redis.Host;
import com.mmmthatsgoodcode.redis.protocol.Command;
import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.model.PinnedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.MultiBulkReply;

public class Blpop extends KeyedCommand<MultiBulkReply>{

	// Blpop is the blocking version of Lpop Command
	private final List<byte[]> keys;
	private final byte[] timeout;
	private Host host = null;
	
	public Blpop(byte[] timeout, String key, byte[]... keys) {
		super(key);
		this.keys = Arrays.asList(keys);
		this.timeout = timeout;
	}

	public List<byte[]> getKeys() {
		return keys;
	}
	
	public byte[] getTimeout() {
		return timeout;
	}
}