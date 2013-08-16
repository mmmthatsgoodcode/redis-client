package com.mmmthatsgoodcode.redis.disruptor.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

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
		
		// see if this can be executed in a pipeline
		if (event.getRequest().canPipe() && client.shouldBatch()) {
		
			LOG.trace("Adding {} to pipeline", event);

			// should it go to a specific pipeline?
			if (event.getHash() != null) {

				pipelines.get( Hashing.consistentHash(event.getHash(), client.getHosts().size()) ).add(event.getRequest());
				
			} else {
				
				pipelines.get(new Random().nextInt(pipelines.size())).add(event.getRequest());
				
			}
			
		} else {
			
			// just forward it to a random host right now
			client.getHosts().get(new Random().nextInt(client.getHosts().size())).schedule(event.getRequest());
			
		}
		
		// batch ended, fire them pipelines!
		if (endOfBatch) {
			
			LOG.debug("Batch done, sending pipelines {}", pipelines);
			int at=0;
			for (Pipeline pipeline:pipelines) {
				if (pipeline.size() > 0) {
					Host selectedHost = client.getHosts().get(at);
					LOG.debug("Forwarding pipeline with {} requests to {}", pipeline.size(), selectedHost);
					selectedHost.schedule(pipeline);
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
