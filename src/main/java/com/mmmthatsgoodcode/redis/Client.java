package com.mmmthatsgoodcode.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.mmmthatsgoodcode.redis.Host.Builder;
import com.mmmthatsgoodcode.redis.client.NoConnectionsAvailableException;
import com.mmmthatsgoodcode.redis.disruptor.processor.RequestEvent;
import com.mmmthatsgoodcode.redis.disruptor.processor.RequestHasher;
import com.mmmthatsgoodcode.redis.disruptor.processor.RequestRouter;
import com.mmmthatsgoodcode.redis.protocol.KeyedRequest;
import com.mmmthatsgoodcode.redis.protocol.PendingResponse;
import com.mmmthatsgoodcode.redis.protocol.Request;

public class Client {

	public static class Builder {
		
		private int connectionsPerHost = 1;
		private List<HostInfo> hosts = new ArrayList<HostInfo>();
		private AtomicBoolean shouldBatch = new AtomicBoolean(true);
		private AtomicBoolean shouldHash = new AtomicBoolean(true);
		private boolean connectionRecovery = true;
		private List<ClientMonitor> monitors = new ArrayList<ClientMonitor>();
		
		public Builder addHost(String hostname, int port) {
		
			hosts.add(new HostInfo(hostname, port));
			
			return this;
		}
		
		public Builder addMonitor(ClientMonitor monitor) {
			if (monitor == null) throw new IllegalArgumentException("Can not add null to connection monitors");
			this.monitors.add(monitor);
			return this;
		}
		
		public Builder withConnectionsPerHost(int connectionsPerHost) {
			if (connectionsPerHost < 1) throw new IllegalArgumentException("Need at least 1 conenction per host!");
			this.connectionsPerHost = connectionsPerHost;
			return this;
		}

		public Builder shouldBatch(boolean shouldBatch) {
			this.shouldBatch.set(shouldBatch);
			return this;
		}
		
		public Builder shouldHash(boolean shouldHash) {
			this.shouldHash.set(shouldHash);
			return this;
		}
		
		public Builder shouldRecoverConnections(boolean connectionRecovery) {
			this.connectionRecovery = connectionRecovery;
			return this;
		}

		
		public Client build() {
			return new Client(hosts, connectionsPerHost, shouldBatch, shouldHash, connectionRecovery, monitors);
		}
		
	}
	
	public static class HostInfo {
		
		private final String hostname;
		private final int port;
		
		public HostInfo(String hostname, int port) {
			this.hostname = hostname;
			this.port = port;
		}
		
		public String getHostname() {
			return hostname;
		}
		
		public int getPort() {
			return this.port;
		}
		
		public String toString() {
			return hostname+":"+port;
		}
		
	}

	protected final List<Host> hosts;
	protected AtomicBoolean shouldBatch;
	protected AtomicBoolean shouldHash;
	protected final List<ClientMonitor> monitors;
	protected final boolean connectionRecovery;
	private final HashFunction hasher = Hashing.murmur3_128();
	
	private Client(List<HostInfo> hosts, int connectionsPerHost, AtomicBoolean shouldBatch, AtomicBoolean shouldHash, boolean connectionRecovery, List<ClientMonitor> monitors) {

		this.hosts = new ArrayList<Host>();
		
		for (HostInfo hostInfo:hosts) {
			this.hosts.add(
					new Host.Builder()
					.setHostInfo(hostInfo)
					.forClient(this)
					.connections(connectionsPerHost)
					.build());
		}
		
		this.shouldBatch = shouldBatch;
		this.shouldHash = shouldHash;
		this.connectionRecovery = connectionRecovery;
		this.monitors = monitors;

		
	}
	
	public List<ClientMonitor> getMonitors() {
		return monitors;
	}
	
	public List<Host> getHosts() {
		return hosts;
	}
	
	public void connect() {
		if (hosts.size() == 0) throw new IllegalStateException("No Hosts to connect to!");
		for(Host host:hosts) {
			host.connect();
		}
	}
	
	public PendingResponse send(KeyedRequest request) throws NoConnectionsAvailableException {

		if (shouldHash()) getHosts().get(Hashing.consistentHash(hasher.hashString(request.getKey()), getHosts().size())).send(request);		
		getHosts().get(new Random().nextInt(getHosts().size())).send(request);
			
		return request.getResponse();
	}
	
	public PendingResponse send(Request request) throws NoConnectionsAvailableException {
		
		getHosts().get(new Random().nextInt(getHosts().size())).send(request);
		
		return request.getResponse();
		
	}
	
	public boolean shouldBatch() {
		return shouldBatch.get();
	}
	
	public boolean shouldHash() {
		return shouldHash.get();
	}
	
	public HashFunction getHasher() {
		return hasher;
	}
	
}
