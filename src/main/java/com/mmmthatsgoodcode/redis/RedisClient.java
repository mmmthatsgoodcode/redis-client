package com.mmmthatsgoodcode.redis;

import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.mmmthatsgoodcode.redis.Host.Builder;
import com.mmmthatsgoodcode.redis.client.NoConnectionsAvailableException;
import com.mmmthatsgoodcode.redis.client.Transaction;
import com.mmmthatsgoodcode.redis.client.monitor.SelfHealingMonitor;
import com.mmmthatsgoodcode.redis.disruptor.processor.CommandEvent;
import com.mmmthatsgoodcode.redis.disruptor.processor.CommandHasher;
import com.mmmthatsgoodcode.redis.disruptor.processor.CommandRouter;
import com.mmmthatsgoodcode.redis.disruptor.processor.CommandEvent.CommandEventTranslator;
import com.mmmthatsgoodcode.redis.protocol.Command;
import com.mmmthatsgoodcode.redis.protocol.Redis2TextProtocol;
import com.mmmthatsgoodcode.redis.protocol.Reply;
import com.mmmthatsgoodcode.redis.protocol.command.Exec;
import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.model.PendingReply;
import com.mmmthatsgoodcode.redis.protocol.model.PinnedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.MultiBulkReply;

public class RedisClient implements Client {

	public static class Builder<C extends RedisClient> {
		
		protected int connectionsPerHost = 1;
		protected List<HostInfo> hosts = new ArrayList<HostInfo>();
		protected AtomicBoolean shouldHash = new AtomicBoolean(true);
		protected boolean connectionRecovery = true;
		protected List<ClientMonitor> monitors = new ArrayList<ClientMonitor>();
		protected Map<ChannelOption, Object> channelOptions = new HashMap<ChannelOption, Object>();
		protected HashFunction hashFunction = Hashing.murmur3_128();
		protected AtomicBoolean withTrafficLogging = new AtomicBoolean(false);
		protected Protocol protocol = new Redis2TextProtocol();
		protected EventLoopGroup eventLoopGroup = null;
		
		public Builder<C> addHost(String hostname, int port) {
		
			hosts.add(new HostInfo(hostname, port));
			
			return this;
		}
		
		public Builder<C> addMonitor(ClientMonitor monitor) {
			if (monitor == null) throw new IllegalArgumentException("Can not add null to connection monitors");
			this.monitors.add(monitor);
			return this;
		}
		
		public Builder<C> withConnectionsPerHost(int connectionsPerHost) {
			if (connectionsPerHost < 1) throw new IllegalArgumentException("Need at least 1 conenction per host!");
			this.connectionsPerHost = connectionsPerHost;
			return this;
		}
		
		public Builder<C> withProtocol(Protocol protocol) {
			if (protocol == null) throw new IllegalArgumentException("Protocol may not be null");
			this.protocol = protocol;
			return this;
		}
		
		public Builder<C> withEventLoopGroup(EventLoopGroup eventLoopGroup) {
			this.eventLoopGroup = eventLoopGroup;
			return this;
		}
		
		public Builder<C> shouldHash(boolean shouldHash) {
			this.shouldHash.set(shouldHash);
			return this;
		}
		
		public Builder<C> shouldRecoverConnections(boolean connectionRecovery) {
			this.connectionRecovery = connectionRecovery;
			return this;
		}
		
		public Builder<C> withHashFunction(HashFunction hashFunction) {
			this.hashFunction = hashFunction;
			return this;
		}

		public Builder<C> withTrafficLogging(boolean withTrafficLogging) {
			this.withTrafficLogging.set(withTrafficLogging);
			return this;
		}
		
		public <T> Builder<C> withChannelOption(ChannelOption<T> channelOption, T value) {
			channelOptions.put(channelOption, value);			
			return this;
		}
		
		public C build() {
			// add some default channel options
			if (!channelOptions.containsKey(ChannelOption.SO_KEEPALIVE)) channelOptions.put(ChannelOption.SO_KEEPALIVE, true);
			if (!channelOptions.containsKey(ChannelOption.CONNECT_TIMEOUT_MILLIS)) channelOptions.put(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
			
			return (C) new RedisClient(hosts, eventLoopGroup, protocol, hashFunction, connectionsPerHost, channelOptions, shouldHash, connectionRecovery, monitors, withTrafficLogging);
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
	protected final List<ClientMonitor> monitors;
	protected final boolean connectionRecovery;
	protected final HashFunction hashFunction;
	protected final Protocol protocol;
	protected final Logger LOG = LoggerFactory.getLogger(RedisClient.class);
	protected final EventLoopGroup eventLoopGroup;
	
	protected RedisClient(List<HostInfo> hosts, EventLoopGroup eventLoopGroup, Protocol protocol, HashFunction hashFunction, int connectionsPerHost, Map<ChannelOption, Object> channelOptions, AtomicBoolean shouldHash, boolean connectionRecovery, List<ClientMonitor> monitors, AtomicBoolean trafficLogging) {

		this.hosts = new ArrayList<Host>();
		this.protocol = protocol;
		this.hashFunction = hashFunction;
		if (eventLoopGroup != null) this.eventLoopGroup = eventLoopGroup;
		else this.eventLoopGroup = new NioEventLoopGroup();
		
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
		
		if (connectionRecovery) this.monitors.add(new SelfHealingMonitor(1000));
		
		
	}
	
	/* (non-Javadoc)
	 * @see com.mmmthatsgoodcode.redis.Client#getMonitors()
	 */
	@Override
	public List<ClientMonitor> getMonitors() {
		return monitors;
	}
	
	/* (non-Javadoc)
	 * @see com.mmmthatsgoodcode.redis.Client#getHosts()
	 */
	@Override
	public List<Host> getHosts() {
		return hosts;
	}
	
	/* (non-Javadoc)
	 * @see com.mmmthatsgoodcode.redis.Client#connect()
	 */
	@Override
	public void connect() {
		if (hosts.size() == 0) throw new IllegalStateException("No Hosts to connect to!");
		for(Host host:hosts) {
			host.connect();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.mmmthatsgoodcode.redis.Client#send(com.mmmthatsgoodcode.redis.client.Transaction)
	 */
	@Override
	public PendingReply<MultiBulkReply> send(Transaction transaction) throws NoConnectionsAvailableException {
		// close transaction with EXEC
		Exec exec = new Exec();
		transaction.add(exec);
		
		send((PinnedCommand<MultiBulkReply>) transaction);
		
		// return the EXEC's reply..
		return exec.getReply();
	}

	/* (non-Javadoc)
	 * @see com.mmmthatsgoodcode.redis.Client#send(com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand)
	 */
	@Override
	public <T extends Reply> PendingReply<T> send(KeyedCommand<T> keyedCommand) throws NoConnectionsAvailableException {

		if (shouldHash()) {
			Host selectedHost = hostForKey(keyedCommand.getKey());
			LOG.debug("Matched key {} to host {}", keyedCommand.getKey(), selectedHost);
			return selectedHost.send(keyedCommand);
		}
		
		// TODO pick a host with a live connection
		return hosts.get(new Random().nextInt(hosts.size())).send(keyedCommand);
		
	}
	
	/* (non-Javadoc)
	 * @see com.mmmthatsgoodcode.redis.Client#send(com.mmmthatsgoodcode.redis.protocol.model.PinnedCommand)
	 */
	@Override
	public <T extends Reply> PendingReply<T> send(PinnedCommand<T> pinnedCommand) throws NoConnectionsAvailableException {
		if (pinnedCommand.getHost() != null) {
			LOG.debug("Sending pinned command to {}", pinnedCommand.getHost());
			return pinnedCommand.getHost().send(pinnedCommand);
		}
		
		// TODO pick a host with a live connection
		return hosts.get(new Random().nextInt(hosts.size())).send(pinnedCommand);		
		
	}
	
	/* (non-Javadoc)
	 * @see com.mmmthatsgoodcode.redis.Client#send(com.mmmthatsgoodcode.redis.protocol.Command)
	 */
	@Override
	public <T extends Reply> PendingReply<T> send(Command<T> command) throws NoConnectionsAvailableException {

		// TODO pick a host with a live connection
		return hosts.get(new Random().nextInt(hosts.size())).send(command);
		
	}
	
	/* (non-Javadoc)
	 * @see com.mmmthatsgoodcode.redis.Client#hostForKey(java.lang.String)
	 */
	@Override
	public Host hostForKey(String key) {
		return hostForHash(hashForKey(key));
	}

	/* (non-Javadoc)
	 * @see com.mmmthatsgoodcode.redis.Client#hashForKey(java.lang.String)
	 */
	@Override
	public HashCode hashForKey(String key) {
		return hashFunction.hashString(key);
	}
	
	/* (non-Javadoc)
	 * @see com.mmmthatsgoodcode.redis.Client#hostForHash(com.google.common.hash.HashCode)
	 */
	@Override
	public Host hostForHash(HashCode hash) {
		if (shouldHash()) return getHosts().get(Hashing.consistentHash(hash, getHosts().size()));
		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.mmmthatsgoodcode.redis.Client#shouldHash()
	 */
	@Override
	public boolean shouldHash() {
		return shouldHash.get();
	}
	
	/* (non-Javadoc)
	 * @see com.mmmthatsgoodcode.redis.Client#trafficLogging()
	 */
	@Override
	public boolean trafficLogging() {
		return trafficLogging.get();
	}
	
	/* (non-Javadoc)
	 * @see com.mmmthatsgoodcode.redis.Client#getProtocol()
	 */
	@Override
	public Protocol getProtocol() {
		return protocol;
	}
	
	/* (non-Javadoc)
	 * @see com.mmmthatsgoodcode.redis.Client#getEventLoopGroup()
	 */
	@Override
	public EventLoopGroup getEventLoopGroup() {
		return this.eventLoopGroup;
	}
	
}
