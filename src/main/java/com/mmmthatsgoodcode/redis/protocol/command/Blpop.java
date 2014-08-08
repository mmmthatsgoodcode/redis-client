package com.mmmthatsgoodcode.redis.protocol.command;

import java.util.Arrays;
import java.util.List;

import com.mmmthatsgoodcode.redis.protocol.Redis2TextProtocol;
import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.MultiBulkReply;

public class Blpop extends KeyedCommand<MultiBulkReply>{

	private final List<String> keys;
	private final byte[] timeout;
	
	public Blpop(byte[] timeout, String key, String... keys) {
		super(key);
		this.keys = Arrays.asList(keys);
		this.timeout = timeout==null?"0".getBytes(Redis2TextProtocol.ENCODING):timeout;
	}

	public List<String> getKeys() {
		return keys;
	}
	
	public byte[] getTimeout() {
		return timeout;
	}
}