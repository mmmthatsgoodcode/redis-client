package com.mmmthatsgoodcode.redis.protocol.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mmmthatsgoodcode.redis.protocol.Reply;
import com.mmmthatsgoodcode.redis.protocol.model.MultiKeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.model.PendingReply;
import com.mmmthatsgoodcode.redis.protocol.model.SplittableCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.BulkReply;
import com.mmmthatsgoodcode.redis.protocol.reply.MultiBulkReply;

public class Mget extends SplittableCommand<Mget, MultiBulkReply>{

	private int i = 0;
	
	public Mget(String key) {
		super(key);
	}
	
	public Mget(List<String> keys){
		super(keys);
	}
	public Mget(Map<String, byte[]> keys){
		super(keys);
	}

	@Override
	public Mget fragment(List<String> keys) {
		this.i++;
		
		final Mget parent = this;
		
		LOG.debug("new Mget created");
		return new Mget(keys){
			final int nb = i;
			
			public String toString(){
				return "Mget-Child-n."+nb;
			}
			
			private final PendingReply<MultiBulkReply> childReply = new PendingReply<MultiBulkReply>(this){
				
				public void finalize(MultiBulkReply partialReply){
					LOG.debug("Mget.fragment(...).new Mget() {...}.childReply.new PendingReply() {...}.finalize()");
					super.finalize(partialReply);
					parent.getReply().finalize(partialReply);
					LOG.debug("back from super.finalize");
				}
			};
			
			public PendingReply<MultiBulkReply> getReply(){
				LOG.debug("getReply invoqued");
				return childReply;
			}
		};
	}

	@Override
	public MultiBulkReply combine(List<MultiBulkReply> partialReplies) {
		List<Reply> returnList = new ArrayList<Reply>();
		Map<String, byte[]> temp = new HashMap<String, byte[]>();
		
		for(String originalKey : getOriginalKeys()){
			for(MultiBulkReply multiBulkReply : partialReplies){
				List<String> sentKeys = ((MultiKeyedCommand) ( multiBulkReply.getCommand() ) ).getKeys();
				
				// if this MultiBulkReply contains the originalKey
				if(sentKeys.contains(originalKey)){
					//adds the Reply
					returnList.add(multiBulkReply.value().get(sentKeys.indexOf(originalKey)));
				}
				//else get the next MultiBulkReply
			}
		}
		
		return new MultiBulkReply(returnList);
	}
	
	public String toString(){
		return "Mget-Parent";
	}
}
