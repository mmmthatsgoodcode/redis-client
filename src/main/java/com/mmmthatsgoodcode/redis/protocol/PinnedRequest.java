package com.mmmthatsgoodcode.redis.protocol;

import com.mmmthatsgoodcode.redis.Host;

public interface PinnedRequest<T extends Response> extends Request<T> {

	public Request pin(Host host);
	public Host getHost();
	
}
