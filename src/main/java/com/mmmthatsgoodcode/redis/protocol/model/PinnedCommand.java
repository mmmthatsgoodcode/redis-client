package com.mmmthatsgoodcode.redis.protocol.model;

import com.mmmthatsgoodcode.redis.Host;
import com.mmmthatsgoodcode.redis.protocol.Command;
import com.mmmthatsgoodcode.redis.protocol.Reply;

public interface PinnedCommand<T extends Reply> extends Command<T> {

	public Command pin(Host host);
	public Host getHost();
	
}
