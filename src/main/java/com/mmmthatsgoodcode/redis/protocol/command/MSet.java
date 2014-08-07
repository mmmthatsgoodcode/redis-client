package com.mmmthatsgoodcode.redis.protocol.command;

import java.util.Set;

import com.mmmthatsgoodcode.redis.Host;
import com.mmmthatsgoodcode.redis.protocol.Command;
import com.mmmthatsgoodcode.redis.protocol.model.PinnedCommand;
import com.mmmthatsgoodcode.redis.protocol.model.SplittableCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.StatusReply;

public class MSet extends SplittableCommand<MSet, StatusReply> implements PinnedCommand<StatusReply> {

	private Host pinnedHost;
	
	public MSet(String key, String[] keys) {
		super(key, keys);
	}

	@Override
	public MSet split(Set<String> keys) {
		
		// get the values from this MSet instanced for "keys"
		
		// create a new MSet  only with these keys&values
		
		
		
		return null;
	}

	@Override
	public Command pin(Host host) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Host getHost() {
		// TODO Auto-generated method stub
		return null;
	}

}
