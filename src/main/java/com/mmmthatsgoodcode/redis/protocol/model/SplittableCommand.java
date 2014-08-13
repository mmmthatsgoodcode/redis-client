package com.mmmthatsgoodcode.redis.protocol.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.redis.protocol.Reply;

public abstract class SplittableCommand<C extends SplittableCommand, T extends Reply> extends MultiKeyedCommand<T> {
	public class PendingSplitReply extends PendingReply<T> {

		private List<T> partialReplies = new ArrayList<T>();
		
		public PendingSplitReply(C command) {
			super(command);
		}
		
		public synchronized void finalize(T partialReply) {
			LOG.debug("SplittableCommand.PendingSplitReply.finalize()");
			partialReplies.add(partialReply);
			LOG.debug("SplitsGet() = "+SplittableCommand.this.splits.get());
			if (SplittableCommand.this.splits.decrementAndGet() == 0) {
				LOG.debug("Starts the combine!");
				this.reply = (T) ((SplittableCommand) getCommand()).combine(partialReplies);
				this.command.replyReceived(this.reply);
				this.lock.release();
			}
		}
	}
	
	protected final Logger LOG = LoggerFactory.getLogger(SplittableCommand.class);
	protected final AtomicInteger splits = new AtomicInteger(0);
	
	@SuppressWarnings("unchecked")
	public SplittableCommand(Map<String, byte[]> keys) {
		super(keys);
		this.reply = this.new PendingSplitReply((C) this);
	}

	public final C split(List<String> keys) {
		this.splits.incrementAndGet();
		return fragment(keys);
	}
	
	public abstract C fragment(List<String> keys);
	public abstract T combine(List<T> partialReplies);
		
}
