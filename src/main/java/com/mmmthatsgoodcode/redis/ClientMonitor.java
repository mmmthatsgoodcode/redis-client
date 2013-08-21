package com.mmmthatsgoodcode.redis;


public interface ClientMonitor {

	public void connectionCreated(Connection connection);
	public void connectionInProgress(Connection connection);
	public void connected(Connection connection);
	public void connectionFailed(Connection connection, Throwable cause);
	public void connectionLost(Connection connection, Throwable cause);
	public void connectionDiscarded(Connection connection, Throwable cause);
	
}
