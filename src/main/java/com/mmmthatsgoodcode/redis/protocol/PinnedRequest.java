package com.mmmthatsgoodcode.redis.protocol;

import com.mmmthatsgoodcode.redis.Host;

public interface PinnedRequest {

	public Request pin(Host host);
	public Host getHost();
	
}
