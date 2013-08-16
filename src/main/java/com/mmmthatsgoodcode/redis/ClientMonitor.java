package com.mmmthatsgoodcode.redis;


public interface ClientMonitor {

	public void connectionCreated(Connection client);
	public void connectionInProgress(Connection client);
	public void connected(Connection client);
	public void connectionFailed(Connection client, Throwable cause);
	public void connectionLost(Connection client, Throwable cause);
	
}
