package com.mmmthatsgoodcode.redis.client.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.redis.Client;
import com.mmmthatsgoodcode.redis.ClientMonitor;

public class LoggingMonitor implements ClientMonitor {

	private static final Logger LOG = LoggerFactory.getLogger(LoggingMonitor.class);
	
	@Override
	public void clientCreated(Client client) {
		LOG.debug("Created Client {}", client);
	}

	@Override
	public void clientConnecting(Client client) {
		LOG.debug("Client connecting {}", client);

	}

	@Override
	public void clientConnectionFailed(Client client, Throwable cause) {
		LOG.debug("Client could not connect {} because {}", client, cause);


	}

	@Override
	public void clientDisconnected(Client client, Throwable cause) {
		LOG.debug("Client disconnected {} because {}", client, cause);

	}

	@Override
	public void clientConnected(Client client) {
		LOG.debug("Client connected {}", client);
		
	}

}
