package com.mmmthatsgoodcode.redis.protocol.command;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.redis.protocol.Reply;
import com.mmmthatsgoodcode.redis.protocol.model.PendingReply;
import com.mmmthatsgoodcode.redis.protocol.model.SplittableCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.MultiBulkReply;

public class SInter extends SplittableCommand<SInter, MultiBulkReply>{

	protected static final Logger LOG = LoggerFactory.getLogger(SInter.class);
	private int i=0;
	
	public SInter(List<String> keys) {
		super(keys);
	}
	
	public SInter(String key) {
		super(key);
	}

	@Override
	protected SInter fragment(List<String> keys) {
		this.i++;
		final SInter parent = this;
		
		LOG.debug("creating new SInter");
		return new SInter(keys){
			final int nb = i;
			
			public String toString(){
				return "Child n"+nb+" "+super.toString();
			}
			
			private final PendingReply<MultiBulkReply> childReply = new PendingReply<MultiBulkReply>(this) {
				
				public void finalize(MultiBulkReply partialReply) {
					LOG.debug("SInter.split(...).new SInter() {...}.getReply().new PendingReply() {...}.finalize()");
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
		LOG.debug("enters Combine()");
		List<Reply> base = partialReplies.get(0).value();
		
		for(MultiBulkReply multiBulkReply : partialReplies){
			if(multiBulkReply.value().size()==0)	return new MultiBulkReply(new ArrayList<Reply>());
			
			base.retainAll(multiBulkReply.value());
		}
		return new MultiBulkReply(base);
	}
	
	public String toString(){
		return "SInter "+getKeys().toString();
	}

}
