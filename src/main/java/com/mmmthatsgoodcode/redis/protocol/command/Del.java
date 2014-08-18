package com.mmmthatsgoodcode.redis.protocol.command;

import java.util.List;

import com.mmmthatsgoodcode.redis.protocol.model.PendingReply;
import com.mmmthatsgoodcode.redis.protocol.model.SplittableCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;

public class Del extends SplittableCommand<Del, IntegerReply>{
	
	private int i = 0;
	
	public Del(List<String> keys) {
		super(keys);
	}
	
	@Override
	public Del fragment(List<String> keys) {
		this.i++;
		
		final Del parent = this;
		
		LOG.debug("new Del created");
		return new Del(keys){
			final int nb = i;
			
			public String toString(){
				return "Del-Child-n."+nb;
			}
			
			private final PendingReply<IntegerReply> childReply = new PendingReply<IntegerReply>(parent){
				
				public void finalize(IntegerReply partialReply){
					LOG.debug("Del.fragment(...).new Del() {...}.childReply.new PendingReply() {...}.finalize()");
					super.finalize(partialReply);
					parent.getReply().finalize(partialReply);
					LOG.debug("back from super.finalize");
				}
			};
			
			public PendingReply<IntegerReply> getReply() {
				LOG.debug("getReply invoqued");
				return childReply;
			}
		};
	}
	
	@Override
	public IntegerReply combine(List<IntegerReply> partialReplies) {
		LOG.debug("Del.combine() : ");
		int deletedKeys = 0;
		
		for(IntegerReply reply : partialReplies){
			deletedKeys += reply.value();
		}
		return new IntegerReply(deletedKeys);
	}
	
	public String toString(){
		return "Del-Parent";
	}
}
