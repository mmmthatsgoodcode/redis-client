package com.mmmthatsgoodcode.redis.disruptor.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.hash.HashFunction;
import com.lmax.disruptor.EventHandler;
import com.mmmthatsgoodcode.redis.Client;
import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;

/**
 * Calculates the consistent Hash of the Command based on the key in the command
 * @author andras
 *
 */
public class CommandHasher implements EventHandler<CommandEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(CommandHasher.class);
	
	private Client client;
	
	public CommandHasher(Client client) {
		this.client = client;
	}
	
	@Override
	public void onEvent(CommandEvent event, long sequence, boolean endOfBatch)
			throws Exception {
		if (client.shouldHash() && event.getCommand() instanceof KeyedCommand) {
			event.setHash(client.hashForKey(((KeyedCommand) event.getCommand()).getKey()));
			LOG.debug("Hashed! {} {}", event, event.getHash());
		}
	}

}
