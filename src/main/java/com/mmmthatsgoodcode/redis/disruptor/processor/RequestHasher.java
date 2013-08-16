package com.mmmthatsgoodcode.redis.disruptor.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.hash.HashFunction;
import com.lmax.disruptor.EventHandler;
import com.mmmthatsgoodcode.redis.Client;
import com.mmmthatsgoodcode.redis.protocol.KeyedRequest;

/**
 * Calculates the consistent Hash of the Request based on the key in the request
 * @author andras
 *
 */
public class RequestHasher implements EventHandler<RequestEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(RequestHasher.class);
	
	private final HashFunction hasher;
	private Client client;
	
	public RequestHasher(Client client, HashFunction hasher) {
		this.hasher = hasher;
		this.client = client;
	}
	
	@Override
	public void onEvent(RequestEvent event, long sequence, boolean endOfBatch)
			throws Exception {
		if (client.shouldHash() && event.getRequest() instanceof KeyedRequest) {
			event.setHash(hasher.hashString(((KeyedRequest) event.getRequest()).getKey()));
			LOG.debug("Hashed! {} {}", event, (endOfBatch?"end":"cont"));
		}
	}

}
