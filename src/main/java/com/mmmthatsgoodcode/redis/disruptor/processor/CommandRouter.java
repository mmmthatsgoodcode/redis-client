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
import com.mmmthatsgoodcode.redis.protocol.model.PinnedCommand;

/**
 * Routes commands to a RedisHost
 * @author andras
 *
 */
public class CommandRouter implements EventHandler<CommandEvent> {

	private final static Logger LOG = LoggerFactory.getLogger(CommandRouter.class);
	private final Client client;
	
	public CommandRouter(Client client) {
		this.client = client;
	}
	
	@Override
	public void onEvent(CommandEvent event, long sequence, boolean endOfBatch)
			throws Exception {
		
			if (PinnedCommand.class.isAssignableFrom(event.getCommand().getClass()) && ((PinnedCommand) event.getCommand()).getHost() != null) {
				((PinnedCommand) event.getCommand()).getHost().send(event.getCommand());
			} else if (event.getHash() != null) {
				client.hostForHash(event.getHash()).send(event.getCommand());
			} else {
				client.getHosts().get(new Random().nextInt(client.getHosts().size())).send(event.getCommand());
			}

	}

}
