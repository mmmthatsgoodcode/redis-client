package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.Host;
import com.mmmthatsgoodcode.redis.protocol.Command;
import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.model.PinnedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Move extends KeyedCommand<IntegerReply> implements PinnedCommand<IntegerReply> {

	private Host host;
	private final byte[] db;
	
	public Move(String key, byte[] db) {
		super(key);
		this.db = db;
		
	}

	@Override
	public Command pin(Host host) {
		this.host = host;
		return this;
	}

	@Override
	public Host getHost() {
		return host;
	}

	public byte[] getDb() {
		return db;
	}
}
