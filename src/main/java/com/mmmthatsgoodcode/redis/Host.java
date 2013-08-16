package com.mmmthatsgoodcode.redis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



import com.lmax.disruptor.WaitStrategy;
import com.mmmthatsgoodcode.redis.Client.HostInfo;
import com.mmmthatsgoodcode.redis.client.NoConnectionsAvailableException;
import com.mmmthatsgoodcode.redis.protocol.Request;

public class Host {

	public static class Builder {
		
		private HostInfo hostInfo = null;
		private int numConnections = 1;
		private Client client;
		private int sendBufferSize = 1024;
		private WaitStrategy sendWaitStrategy;
		
		public Builder forClient(Client client) {
			if (client == null) throw new IllegalArgumentException("Client may not be null");
			this.client = client;
			return this;
		}
		
		public Builder setHostInfo(HostInfo hostInfo) {
			if (hostInfo == null) throw new IllegalArgumentException("HostInfo may not be null");
			this.hostInfo = hostInfo;
			return this;
		}

		public Builder connections(int connections) {
			if (connections < 1) throw new IllegalArgumentException("Need to estabilish at least 1 connection..");
			this.numConnections = connections;
			return this;
		}
		
		public Builder withSendBufferSize(int sendBufferSize) {
			if (sendBufferSize < Client.Builder.MIN_SENDING_BUFFER_SIZE) throw new IllegalArgumentException("Send buffer size may not be smaller than "+Client.Builder.MIN_SENDING_BUFFER_SIZE);
			this.sendBufferSize = sendBufferSize;
			return this;
		}
		
		public Builder withSendWaitStrategy(WaitStrategy sendWaitStrategy) {
			if (sendWaitStrategy == null) throw new IllegalArgumentException("Send wait strategy may not be null");
			this.sendWaitStrategy = sendWaitStrategy;
			return this;			
		}
		
		public Host build() {
			
			forClient(client);
			withSendBufferSize(sendBufferSize);
			
			Host host = new Host(client, hostInfo);
			
			for(int c=1; c<=numConnections; c++) {
				host.createConnection(sendWaitStrategy, sendBufferSize);
			}
			
			return host;		
			
		}
		
		
		
	}
	
	private List<Connection> connections = new ArrayList<Connection>();
	private final Client client;
	private final HostInfo hostInfo;
	
	private Host(Client client, HostInfo hostInfo) {
		this.client = client;
		this.hostInfo = hostInfo;
	}
	
	protected void createConnection(WaitStrategy sendWaitStrategy, int sendBufferSize) {
		connections.add(new Connection(this, sendWaitStrategy, sendBufferSize ));
	}
	
	public Client getClient() {
		return client;
	}
	
	public HostInfo getHostInfo() {
		return this.hostInfo;
	}
	
	public String toString() {
		return hostInfo.toString();
	}
	
	public void schedule(Request request) throws NoConnectionsAvailableException {
		if (connections.size() == 0) throw new IllegalStateException("No connections!");
		if (connections.size() > 1) {
			// TODO selection strategy ?
			List<Connection> eligibleConnections = new ArrayList<Connection>(connections.size());
			for(Connection connection:connections) {
				if (connection.getState() == Connection.State.CONNECTED) eligibleConnections.add(connection);
			}
			
			if (eligibleConnections.size() == 0) throw new NoConnectionsAvailableException();
			
			Collections.shuffle(eligibleConnections);
			eligibleConnections.get(0).schedule(request);
			
		} else {
			connections.get(0).schedule(request);;
		}
		
	}
	
}
