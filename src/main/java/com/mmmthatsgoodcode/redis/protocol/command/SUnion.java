package com.mmmthatsgoodcode.redis.protocol.command;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.redis.protocol.Reply;
import com.mmmthatsgoodcode.redis.protocol.model.PendingReply;
import com.mmmthatsgoodcode.redis.protocol.model.SplittableCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.MultiBulkReply;

public class SUnion extends SplittableCommand<SUnion, MultiBulkReply> {
	
	protected static final Logger LOG = LoggerFactory.getLogger(SUnion.class);
	private int i = 0;
	
	public SUnion(String key){
		super(key);
	}
	
	public SUnion(List<String> key){
		super(key);
	}

	@Override
	protected SUnion fragment(List<String> keys) {
		this.i++;
		final SUnion parent = this;
		
		LOG.debug("Creating new SUnion");
		return new SUnion(keys){
			final int nb = i;
			
			public String toString(){
				return "Child n"+nb+" "+super.toString();
			}
			
			private final PendingReply<MultiBulkReply> childReply = new PendingReply<MultiBulkReply>(this){
				
				public void finalize(MultiBulkReply partialReply){
					LOG.debug("SUnion.split(...).new SUnion() {...}.getReply().new PendingReply() {...}.finalize()");
					super.finalize(partialReply);
					parent.getReply().finalize(partialReply);
					LOG.debug("back from super.finalize()");
				}
			};
			
			public PendingReply<MultiBulkReply> getReply(){
				LOG.debug("getReply() invoked");
				return childReply;
			}
		};
	}

	@Override
	public MultiBulkReply combine(List<MultiBulkReply> partialReplies) {
		LOG.debug("Combine() started!");
		List<Reply> returnList = new ArrayList<Reply>();
		
		for(MultiBulkReply multiBulkReply : partialReplies){
			for(Reply bulkReply : multiBulkReply.value()){
				if(!returnList.contains(bulkReply)){
					returnList.add(bulkReply);
				}
			}
		}
		LOG.debug("size() = {}",returnList.size());
		return new MultiBulkReply(returnList);
	}
	
	public String toString(){
		return "SUnion";
	}
}
