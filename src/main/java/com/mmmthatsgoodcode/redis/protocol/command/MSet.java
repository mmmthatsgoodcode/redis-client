package com.mmmthatsgoodcode.redis.protocol.command;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.mmmthatsgoodcode.redis.protocol.model.PendingReply;
import com.mmmthatsgoodcode.redis.protocol.model.SplittableCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.StatusReply;

public class MSet extends SplittableCommand<MSet, StatusReply>{

	private Map<String,byte[]> keysValues;
	protected static final Logger LOG = LoggerFactory.getLogger(MSet.class);
	private int i = 0;
	
	public MSet(LinkedHashMap<String, byte[]> keyValues) {
		super(ImmutableList.copyOf(keyValues.keySet()));
		this.keysValues = keyValues;
	}
	
	@Override
	protected MSet fragment(List<String> keys) {
		this.i++;
		// get the values from this MSet instanced for "keys"
		LinkedHashMap<String, byte[]> temp = new LinkedHashMap<String, byte[]>();
		for(String key : keys){
			temp.put(key, this.keysValues.get(key));
		}
		
		final MSet parent = this;
		
		// create a new MSet  only with these keys&values
		LOG.debug("Creating new MSet");
		return new MSet(temp) {
			final int nb = i;
			
			public String toString(){
				return "Child n"+nb+super.toString();
			}
			private final PendingReply<StatusReply> childReply = new PendingReply<StatusReply>(this) {
				
				public void finalize(StatusReply partialReply) {
					LOG.debug("MSet.split(...).new MSet() {...}.getReply().new PendingReply() {...}.finalize()");
					super.finalize(partialReply);
					parent.getReply().finalize(partialReply);
					LOG.debug("back from super.finalize()");
				}
				
			};
			
			public PendingReply<StatusReply> getReply() {
				LOG.debug("getReply() invoked");
				return childReply;
			}
		};
	}

	public Map<String, byte[]> getKeysValues() {
		return keysValues;
	}

	@Override
	public StatusReply combine(List<StatusReply> partialReplies) {
		/*
		for (StatusReply partialReply)
		*/
		LOG.debug("MSet.combine() : {}",this.getReply().getClass());
		return new StatusReply("OK");
	}

	public String toString(){
		return "MSet "+keysValues.toString();
	}
}
