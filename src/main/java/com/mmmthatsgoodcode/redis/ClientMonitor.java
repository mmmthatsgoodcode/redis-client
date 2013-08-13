package com.mmmthatsgoodcode.redis;

public interface ClientMonitor {

	public void clientCreated(Client client);
	public void clientConnecting(Client client);
	public void clientConnected(Client client);
	public void clientConnectionFailed(Client client, Throwable cause);
	public void clientDisconnected(Client client, Throwable cause);
	
}
