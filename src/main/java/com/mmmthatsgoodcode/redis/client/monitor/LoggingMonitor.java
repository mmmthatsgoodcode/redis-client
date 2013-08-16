package com.mmmthatsgoodcode.redis.client.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.redis.ClientMonitor;
import com.mmmthatsgoodcode.redis.Connection;

public class LoggingMonitor implements ClientMonitor {

	private static final Logger LOG = LoggerFactory.getLogger(LoggingMonitor.class);
	
	@Override
	public void connectionCreated(Connection connection) {
		LOG.debug("Created Connection {}", connection);
	}

	@Override
	public void connectionInProgress(Connection connection) {
		LOG.debug("Connection in progress {}", connection);

	}

	@Override
	public void connectionFailed(Connection connection, Throwable cause) {
		LOG.debug("Connection {} failed because {}", connection, cause);


	}

	@Override
	public void connectionLost(Connection connection, Throwable cause) {
		LOG.debug("Connection {} lost because {}", connection, cause);

	}

	@Override
	public void connected(Connection connection) {
		LOG.debug("Connection {} estabilished", connection);
		
	}

}
