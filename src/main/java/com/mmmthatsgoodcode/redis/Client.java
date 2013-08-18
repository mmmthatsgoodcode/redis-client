package com.mmmthatsgoodcode.redis;

import io.netty.channel.ChannelOption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.mmmthatsgoodcode.redis.Host.Builder;
import com.mmmthatsgoodcode.redis.client.Transaction;
import com.mmmthatsgoodcode.redis.disruptor.processor.RequestEvent;
import com.mmmthatsgoodcode.redis.disruptor.processor.RequestHasher;
import com.mmmthatsgoodcode.redis.disruptor.processor.RequestRouter;
import com.mmmthatsgoodcode.redis.disruptor.processor.RequestEvent.RequestEventTranslator;
import com.mmmthatsgoodcode.redis.protocol.KeyedRequest;
import com.mmmthatsgoodcode.redis.protocol.PendingResponse;
import com.mmmthatsgoodcode.redis.protocol.Request;
import com.mmmthatsgoodcode.redis.protocol.Response;
import com.mmmthatsgoodcode.redis.protocol.request.Exec;
import com.mmmthatsgoodcode.redis.protocol.response.MultiBulkResponse;

public class Client {

	public static class Builder {
		
		public final static int MIN_PROCESSING_BUFFER_SIZE = 2*Runtime.getRuntime().availableProcessors();
		
		private int connectionsPerHost = 1;
		private int processingBufferSize = 1024;
		private List<HostInfo> hosts = new ArrayList<HostInfo>();
		private AtomicBoolean shouldHash = new AtomicBoolean(true);
		private boolean connectionRecovery = true;
		private WaitStrategy processingWaitStrategy = new SleepingWaitStrategy();
		private List<ClientMonitor> monitors = new ArrayList<ClientMonitor>();
		private Map<ChannelOption, Object> channelOptions = new HashMap<ChannelOption, Object>();
		private HashFunction hashFunction = Hashing.murmur3_128();
		private AtomicBoolean withTrafficLogging = new AtomicBoolean(false);
		
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
		
		public Builder shouldHash(boolean shouldHash) {
			this.shouldHash.set(shouldHash);
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
		
		public Builder withHashFunction(HashFunction hashFunction) {
			this.hashFunction = hashFunction;
			return this;
		}

		public Builder withTrafficLogging(boolean withTrafficLogging) {
			this.withTrafficLogging.set(withTrafficLogging);
			return this;
		}
		
		public <T> Builder withChannelOption(ChannelOption<T> channelOption, T value) {
			channelOptions.put(channelOption, value);			
			return this;
		}
		
		public Client build() {
			// add some default channel options
			if (!channelOptions.containsKey(ChannelOption.SO_KEEPALIVE)) channelOptions.put(ChannelOption.SO_KEEPALIVE, true);
			if (!channelOptions.containsKey(ChannelOption.CONNECT_TIMEOUT_MILLIS)) channelOptions.put(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
			
			return new Client(hosts, hashFunction, connectionsPerHost, channelOptions, shouldHash, connectionRecovery, processingWaitStrategy, processingBufferSize, monitors, withTrafficLogging);
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
	protected AtomicBoolean shouldHash;
	protected AtomicBoolean trafficLogging;
	protected final RingBuffer<RequestEvent> processingBuffer;
	protected final List<ClientMonitor> monitors;
	protected final boolean connectionRecovery;
	protected ExecutorService processors = Executors.newFixedThreadPool(2);
	protected final HashFunction hashFunction;

	private Client(List<HostInfo> hosts, HashFunction hashFunction, int connectionsPerHost, Map<ChannelOption, Object> channelOptions, AtomicBoolean shouldHash, boolean connectionRecovery, WaitStrategy processingWaitStrategy, int processingBufferSize, List<ClientMonitor> monitors, AtomicBoolean trafficLogging) {

		this.hosts = new ArrayList<Host>();
		this.hashFunction = hashFunction;
		
		for (HostInfo hostInfo:hosts) {
			this.hosts.add(
					new Host.Builder()
					.setHostInfo(hostInfo)
					.forClient(this)
					.createConnections(connectionsPerHost)
					.withChannelOptions(channelOptions)
					.build());
		}
		
		this.shouldHash = shouldHash;
		this.trafficLogging = trafficLogging;
		this.connectionRecovery = connectionRecovery;
		this.monitors = monitors;
		
		// create processing buffer
		processingBuffer = RingBuffer.createMultiProducer(RequestEvent.EVENT_FACTORY, processingBufferSize, processingWaitStrategy);

		// create processors
		BatchEventProcessor<RequestEvent> hasher = new BatchEventProcessor<RequestEvent>( processingBuffer, processingBuffer.newBarrier(), new RequestHasher(this ) );
		BatchEventProcessor<RequestEvent> router = new BatchEventProcessor<RequestEvent>( processingBuffer, processingBuffer.newBarrier(hasher.getSequence()), new RequestRouter(this));
		
		// start processors
		processors.execute(hasher);
		processors.execute(router);
		
		processingBuffer.addGatingSequences(router.getSequence());
		
		
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
	
	public <T extends Response> PendingResponse<T> send(Request<T> request) {
		
		processingBuffer.publishEvent(new RequestEvent.RequestEventTranslator(request));
		return request.getResponse();
		
	}
	
	public PendingResponse<MultiBulkResponse> send(Transaction transaction) {
		
		// close transaction with EXEC
		Exec exec = new Exec();
		transaction.add(exec);
		
		processingBuffer.publishEvent(new RequestEvent.RequestEventTranslator(transaction));
		return exec.getResponse();
		
	}
	
	public Host hostForKey(String key) {
		if (shouldHash()) return hostForHash(hashForKey(key));
		return null;
	}

	public HashCode hashForKey(String key) {
		if (shouldHash()) return hashFunction.hashString(key);
		return null;
	}
	
	public Host hostForHash(HashCode hash) {
		if (shouldHash()) return getHosts().get(Hashing.consistentHash(hash, getHosts().size()));
		return null;
	}
	
	public boolean shouldHash() {
		return shouldHash.get();
	}
	
	public boolean trafficLogging() {
		return trafficLogging.get();
	}
	
}
