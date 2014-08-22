package com.mmmthatsgoodcode.redis.protocol.command;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.redis.protocol.model.PendingReply;
import com.mmmthatsgoodcode.redis.protocol.model.SplittableCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Del extends SplittableCommand<Del, IntegerReply>{

	protected static final Logger LOG = LoggerFactory.getLogger(Del.class);
	private int i=0;
	
	public Del(List<String> keys) {
		super(keys);
	}
	
	public Del(String key){
		super(key);
	}

	@Override
	protected Del fragment(List<String> keys) {
		this.i++;
		
		final Del parent = this;
		
		LOG.debug("Creating new Del");
		return new Del(keys){
			final int nb = i;
			
			public String toString(){
				return "Child n."+nb+" "+super.toString();
			}
			
			private final PendingReply<IntegerReply> childReply = new PendingReply<IntegerReply>(this){
				
				public void finalize(IntegerReply partialReply){
					LOG.debug("Del.fragment(...).new Del() {...}.childReply.new PendingReply() {...}.finalize()");
					super.finalize(partialReply);
					parent.getReply().finalize(partialReply);
					LOG.debug("back from super.finalize()");
				}
			};
			
			public PendingReply<IntegerReply> getReply(){
				LOG.debug("getReply() invoked");
				return childReply;
			}
		};
	}

	@Override
	public IntegerReply combine(List<IntegerReply> partialReplies) {
		LOG.debug("Del.combine()");
		int deletedKeys=0;
		
		for(IntegerReply reply : partialReplies){
			deletedKeys +=reply.value();
		}
		return new IntegerReply(deletedKeys);
	}

	public String toString(){
		return "Del";
	}
}
