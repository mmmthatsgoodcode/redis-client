package com.mmmthatsgoodcode.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.mmmthatsgoodcode.redis.client.monitor.SelfHealingMonitor;

public class ClientGroup implements Iterable<Client> {

	
	public static class Builder {
		
		private class RedisNode {
			
			private String host;
			private int port = 6379;
			
			public RedisNode(String hostname) {
				this.host = hostname;
			}
			
			public RedisNode(String hostname, int port) {
				this(hostname);
				this.port = port;
			}
			
			public String getHost() {
				return host;
			}
			
			public int getPort() {
				return port;
			}
				
		}
		
		private List<RedisNode> nodes = new ArrayList<RedisNode>();
		private List<ClientMonitor> monitors = new ArrayList<ClientMonitor>();
		private int connectionPerServer = 1;
		private HashFunction hashFunction = Hashing.murmur3_128();
		
		public Builder addServer(String hostname, int port) {
			nodes.add(new RedisNode(hostname, port));
			return this;
		}
		
		public Builder connectionsPerServer(int connectionsPerServer) {
			if (connectionsPerServer < 1) throw new IllegalArgumentException("connectionsPerServer may not be < 1");
			this.connectionPerServer = connectionsPerServer;
			return this;
		}
		
		public Builder withHashFunction(HashFunction hashFunction) {
			if (hashFunction == null) throw new IllegalArgumentException("hashFunction may not be null");
			this.hashFunction = hashFunction;
			return this;
		}
		
		public Builder addMonitor(ClientMonitor monitor) {
			monitors.add(monitor);
			return this;
		}
		
		public ClientGroup build() {
			
			monitors.add(new SelfHealingMonitor(1000));
			
			ClientGroup clientGroup = new ClientGroup(this.hashFunction);
			
			for (RedisNode node:nodes) {
				clientGroup.addClient(node.getHost()+":"+node.getPort(), new Client(node.getHost(), node.getPort(), monitors));
			}
			
			return clientGroup;
			
			
		}
		
		
	}
	
	private Map<String, List<Client>> hosts = new HashMap<String, List<Client>>();
	private final HashFunction hashFunction;
	
	private ClientGroup(HashFunction hashFunction) {
		this.hashFunction = hashFunction;
	}
	
	private synchronized void addClient(String host, Client client) {
		if (!this.hosts.containsKey(host)) this.hosts.put(host, new ArrayList<Client>());
		this.hosts.get(host).add(client);
	}
	
	public ClientGroup connect() {
		for(Client client:this) {
			client.connect();
		}
		
		return this;
	}
	
	public Client forKey(String key) {
		return null;
	}

	@Override
	public Iterator<Client> iterator() {
		List<Client> clients = new ArrayList<Client>();
		for(List<Client> clientList:hosts.values()) {
			clients.addAll(clientList);
		}
		
		return clients.iterator();
	}
	
}
