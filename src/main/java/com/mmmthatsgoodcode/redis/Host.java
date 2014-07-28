package com.mmmthatsgoodcode.redis;

import io.netty.channel.ChannelOption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;








import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.WaitStrategy;
import com.mmmthatsgoodcode.redis.RedisClient.HostInfo;
import com.mmmthatsgoodcode.redis.client.NoConnectionsAvailableException;
import com.mmmthatsgoodcode.redis.client.Transaction;
import com.mmmthatsgoodcode.redis.protocol.Command;
import com.mmmthatsgoodcode.redis.protocol.Reply;
import com.mmmthatsgoodcode.redis.protocol.command.Exec;
import com.mmmthatsgoodcode.redis.protocol.model.PendingReply;
import com.mmmthatsgoodcode.redis.protocol.reply.MultiBulkReply;

public class Host {

	public static class Builder {
		
		private HostInfo hostInfo = null;
		private int numConnections = 1;
		private Client client;
		private int sendBufferSize = 1024;
		private WaitStrategy sendWaitStrategy;
		private Map<ChannelOption, Object> channelOptions = new HashMap<ChannelOption, Object>();
		
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

		public Builder createConnections(int connections) {
			if (connections < 1) throw new IllegalArgumentException("Need to estabilish at least 1 connection..");
			this.numConnections = connections;
			return this;
		}
		
		public Builder withChannelOptions(Map<ChannelOption, Object> channelOptions) {
			if (channelOptions == null) throw new IllegalArgumentException("ChannelOptions may not be null");
			this.channelOptions = channelOptions;
			
			return this;
		}
		
		public Host build() {
			
			forClient(client);
			
			Host host = new Host(client, hostInfo, channelOptions);
			
			for(int c=1; c<=numConnections; c++) {
				host.createConnection(channelOptions);
			}
			
			return host;		
			
		}
		
		
		
	}
	
	private List<Connection> connections = new ArrayList<Connection>();
	private final Map<ChannelOption, Object> channelOptions;
	private final Client client;
	private final HostInfo hostInfo;
	private final static Logger LOG = LoggerFactory.getLogger(Host.class);
	
	private Host(Client client, HostInfo hostInfo, Map<ChannelOption, Object> channelOptions) {
		this.client = client;
		this.hostInfo = hostInfo;
		this.channelOptions = channelOptions;
	}
	
	public Connection createConnection() {
		return createConnection(channelOptions);
	}
	
	public Connection createConnection(Map<ChannelOption, Object> channelOptions) {
		Connection connection = new Connection(this, channelOptions);
		connections.add(connection);
		
		return connection;
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
	
	public <T extends Reply> PendingReply<T> send(Command<T> command) throws NoConnectionsAvailableException {
		LOG.debug("Outgoing command {}", command);
		if (connections.size() == 0) {
			LOG.error("Attempted to schedule command {} with no Connections available!");
			throw new IllegalStateException("No connections!");
		}
		
		Connection selectedConnection = getAvailbleConnection();
	
		LOG.debug("Selected connection {}", selectedConnection);
		return selectedConnection.send(command);
		
	}
	
	public PendingReply<MultiBulkReply> send(Transaction transaction) throws NoConnectionsAvailableException {
		LOG.debug("Incoming Transaction {}", transaction);
		
		// close transaction with EXEC
		Exec exec = new Exec();
		transaction.add(exec);
		
		send(transaction);
		
		// return the EXEC's reply..
		return exec.getReply();
		
	}
	
	/**
	 * TODO ConnectionSelectionStrategy?
	 * @return
	 * @throws NoConnectionsAvailableException 
	 */
	public Connection getAvailbleConnection() throws NoConnectionsAvailableException {
		
		if (connections.size() > 1) {
		
			// filter connections
			List<Connection> eligibleConnections = new ArrayList<Connection>(connections.size());
			for(Connection connection:connections) {
				if (connection.getState() == Connection.State.CONNECTED) eligibleConnections.add(connection);
			}
			
			if (eligibleConnections.size() == 0) throw new NoConnectionsAvailableException();
			
			Collections.shuffle(eligibleConnections);
			return eligibleConnections.get(0);
		
		} else if (connections.get(0).getState() == Connection.State.CONNECTED) {
			
			return connections.get(0);
			
		}
		
		throw new NoConnectionsAvailableException();
		
	}
	
	public void connect() {
		if (connections.size() == 0) throw new IllegalStateException("No Connections to connect!");
		for (Connection connection:connections) {
			connection.connect();
		}
		
	}
	
}
