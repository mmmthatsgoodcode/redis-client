package com.mmmthatsgoodcode.redis.protocol;

import com.mmmthatsgoodcode.redis.Host;

public interface PinnedCommand<T extends Reply> extends Command<T> {

	public Command pin(Host host);
	public Host getHost();
	
}
