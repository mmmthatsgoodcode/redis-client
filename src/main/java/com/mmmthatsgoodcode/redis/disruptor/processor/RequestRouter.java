package com.mmmthatsgoodcode.redis.disruptor.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.hash.Hashing;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.mmmthatsgoodcode.redis.Client;
import com.mmmthatsgoodcode.redis.Host;
import com.mmmthatsgoodcode.redis.protocol.Pipeline;

/**
 * Routes requests to a RedisHost
 * @author andras
 *
 */
public class RequestRouter implements EventHandler<RequestEvent> {

	private final static Logger LOG = LoggerFactory.getLogger(RequestRouter.class);
	private final Client client;
	private final List<Pipeline> pipelines = new ArrayList<Pipeline>();
	
	public RequestRouter(Client client) {
		this.client = client;
		initPipelines();
	}
	
	@Override
	public void onEvent(RequestEvent event, long sequence, boolean endOfBatch)
			throws Exception {
		
		LOG.debug("Adding {} to batch", event);
		
		pipelines.get( Hashing.consistentHash(event.getHash(), client.getHosts().size()) ).add(event.getRequest());
		
		if (endOfBatch) {
			
			LOG.debug("Batch done, sending {} pipelines", pipelines.size());
			int at=0;
			for (Pipeline pipeline:pipelines) {
				if (pipeline.size() > 0) {
					LOG.debug("Forwarding pipeline with {} requests", pipeline.size());
					client.getHosts().get(at).schedule(pipeline);
				}
				at++;
			}
			
			// prepare pipelines for next batch
			initPipelines();
		}
	}
	
	private void initPipelines() {
		pipelines.clear();
		// Maintain one pipeline per host
		for (Host host:client.getHosts() ) {
			pipelines.add(new Pipeline());
		}		
	}

}
