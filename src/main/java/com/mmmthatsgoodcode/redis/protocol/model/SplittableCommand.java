package com.mmmthatsgoodcode.redis.protocol.model;

import java.util.Set;

import com.mmmthatsgoodcode.redis.protocol.Command;
import com.mmmthatsgoodcode.redis.protocol.Reply;

public abstract class SplittableCommand<C extends MultiKeyedCommand, T extends Reply> extends MultiKeyedCommand<T> {
	
	public static class PendingSplitReply<T extends Reply> extends PendingReply<T> {

		public PendingSplitReply(Command<T> command) {
			super(command);
		}
	}
	
	public SplittableCommand(String key, String[] keys) {
		super(key, keys);
		
		this.reply = new PendingSplitReply<T>(this);
	}

	public abstract C split(Set<String> keys);
}
