package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.Redis2TextProtocol;
import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.BulkReply;

public class Srandmember extends KeyedCommand<BulkReply> {

	private final byte[] count;
	
	public Srandmember(String key) {
		super(key);
		this.count = "1".getBytes(Redis2TextProtocol.ENCODING);
	}
	
	public Srandmember(String key, byte[] count) {
		super(key);
		this.count = count;
	}
	
	public byte[] getCount() {
		return count;
	}
}
