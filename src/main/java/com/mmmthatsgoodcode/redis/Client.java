package com.mmmthatsgoodcode.redis;

import java.util.ArrayList;
import java.util.List;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.mmmthatsgoodcode.redis.Host.Builder;
import com.mmmthatsgoodcode.redis.disruptor.processor.RequestEvent;

public class Client {

	public static class Builder {
		
		public final static int MIN_PROCESSING_BUFFER_SIZE = 2*Runtime.getRuntime().availableProcessors();
		public final static int MIN_SENDING_BUFFER_SIZE = 2*Runtime.getRuntime().availableProcessors();
		
		private int connectionsPerHost = 1;
		private int processingBufferSize = 1024;
		private int sendBufferSize = 1024;
		private List<HostInfo> hosts = new ArrayList<HostInfo>();
		private boolean shouldBatch = true;
		private boolean shouldHash = true;
		private boolean connectionRecovery = true;
		private WaitStrategy processingWaitStrategy = new SleepingWaitStrategy();
		private WaitStrategy sendWaitStrategy = new SleepingWaitStrategy();
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
		
		public Builder withProcessingBufferSize(int processingBufferSize) {
			if (processingBufferSize < MIN_PROCESSING_BUFFER_SIZE) throw new IllegalArgumentException("Processing buffer size may not be smaller than "+MIN_PROCESSING_BUFFER_SIZE);
			this.processingBufferSize = processingBufferSize;
			return this;
		}
		
		public Builder withSendBufferSize(int sendBufferSize) {
			if (sendBufferSize < MIN_SENDING_BUFFER_SIZE) throw new IllegalArgumentException("Send buffer size may not be smaller than "+MIN_SENDING_BUFFER_SIZE);
			this.sendBufferSize = sendBufferSize;
			return this;			
		}
		
		public Builder shouldBatch(boolean shouldBatch) {
			this.shouldBatch = shouldBatch;
			return this;
		}
		
		public Builder shouldHash(boolean shouldHash) {
			this.shouldHash = shouldHash;
			return this;
		}
		
		public Builder shouldRecoverConnections(boolean connectionRecovery) {
			this.connectionRecovery = connectionRecovery;
			return this;
		}
		
		public Builder withProcessingWaitStrategy(WaitStrategy processingWaitStrategy) {
			if (processingWaitStrategy == null) throw new IllegalArgumentException("Processing wait strategy may not be null");
			this.processingWaitStrategy = processingWaitStrategy;
			return this;
		}
		
		public Builder withSendWaitStrategy(WaitStrategy sendWaitStrategy) {
			if (sendWaitStrategy == null) throw new IllegalArgumentException("Send wait strategy may not be null");
			this.sendWaitStrategy = sendWaitStrategy;
			return this;
		}
		
		public Client build() {
			return new Client(hosts, connectionsPerHost, shouldBatch, shouldHash, connectionRecovery, processingWaitStrategy, processingBufferSize, sendWaitStrategy, sendBufferSize, monitors);
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
		
	}

	protected final List<Host> hosts;
	protected volatile boolean shouldBatch;
	protected volatile boolean shouldHash;
	protected final RingBuffer<RequestEvent> processingBuffer;
	protected final List<ClientMonitor> monitors;
	protected final boolean connectionRecovery;
	
	private Client(List<HostInfo> hosts, int connectionsPerHost, boolean shouldBatch, boolean shouldHash, boolean connectionRecovery, WaitStrategy processingWaitStrategy, int processingBufferSize, WaitStrategy sendWaitStrategy, int sendBufferSize, List<ClientMonitor> monitors) {

		this.hosts = new ArrayList<Host>();
		
		for (HostInfo hostInfo:hosts) {
			this.hosts.add(
					new Host.Builder()
					.setHostInfo(hostInfo)
					.connections(connectionsPerHost)
					.withSendBufferSize(sendBufferSize)
					.withSendWaitStrategy(sendWaitStrategy)
					.build());
		}
		
		this.shouldBatch = shouldBatch;
		this.shouldHash = shouldHash;
		this.connectionRecovery = connectionRecovery;
		this.processingBuffer = RingBuffer.createMultiProducer(RequestEvent.EVENT_FACTORY, processingBufferSize, processingWaitStrategy);
		this.monitors = monitors;
	}
	
	public List<ClientMonitor> getMonitors() {
		return monitors;
	}
	
	public List<Host> getHosts() {
		return hosts;
	}
	
}
