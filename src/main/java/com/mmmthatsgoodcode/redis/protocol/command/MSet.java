package com.mmmthatsgoodcode.redis.protocol.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.redis.protocol.model.PendingReply;
import com.mmmthatsgoodcode.redis.protocol.model.SplittableCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.StatusReply;

public class MSet extends SplittableCommand<MSet, StatusReply>{

	private Map<String,byte[]> keysValues;
	protected static final Logger LOG = LoggerFactory.getLogger(MSet.class);
	private int i = 0;
	
	public MSet(Map<String, byte[]> keyValues) {
		super(keyValues);
		this.keysValues = keyValues;
	}
	
	@Override
	protected MSet fragment(List<String> keys) {
		this.i++;
		// get the values from this MSet instanced for "keys"
		Map<String, byte[]> temp = new HashMap<String, byte[]>();
		for(String key : keys){
			temp.put(key, this.keysValues.get(key));
		}
		
		final MSet parent = this;
		
		// create a new MSet  only with these keys&values
		LOG.debug("new MSet created");
		return new MSet(temp) {
			final int nb = i;
			
			public String toString(){
				return "Child n."+nb;
			}
			private final PendingReply<StatusReply> childReply = new PendingReply<StatusReply>(parent) {
				
				public void finalize(StatusReply partialReply) {
					LOG.debug("MSet.split(...).new MSet() {...}.getReply().new PendingReply() {...}.finalize()");
					parent.getReply().finalize(partialReply);
					super.finalize(partialReply);
					LOG.debug("back from super.finalize");
				}
				
			};
			
			public PendingReply<StatusReply> getReply() {
				LOG.debug("getReply invoqued");
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
		return "parent";
	}
}
