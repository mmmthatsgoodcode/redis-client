package com.mmmthatsgoodcode.redis;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.redis.client.ClientWriteHandler;
import com.mmmthatsgoodcode.redis.client.RequestEncoder;
import com.mmmthatsgoodcode.redis.client.RequestFulfiller;
import com.mmmthatsgoodcode.redis.client.RequestLogger;
import com.mmmthatsgoodcode.redis.client.ResponseDecoder;
import com.mmmthatsgoodcode.redis.client.ResponseLogger;
import com.mmmthatsgoodcode.redis.protocol.Request;

/**
 * Represents a single connection a RedisHost.
 * Buffers outbound requests on a RingBuffer
 * @author andras
 *
 */
public class Connection  {
	
	
	public enum State { CREATED, CONNECTING, CONNECTED, DISCONNECTED }

	protected volatile Connection.State state = State.CREATED;		
	private Channel channel = null;
	private Bootstrap bootstrap = new Bootstrap();
	private final Host host;
	private static final Logger LOG = LoggerFactory.getLogger(Connection.class);

	public static final AttributeKey<BlockingQueue<Request>> OUTBOUND = new AttributeKey<BlockingQueue<Request>>("out");

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Connection(Host host, Map<ChannelOption, Object> channelOptions) {
		
		this.host = host;
		
		bootstrap.group(new NioEventLoopGroup());
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.handler(new ChannelInitializer<SocketChannel> () {

			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				
				// this will hold the sent requests on the Channel
				ch.attr(OUTBOUND).set(new LinkedBlockingQueue<Request>());
				if (getHost().getClient().trafficLogging()) ch.pipeline().addLast(new RequestLogger(), new RequestEncoder(), new ClientWriteHandler(), new ResponseLogger(), new ResponseDecoder(), new RequestFulfiller());
				else ch.pipeline().addLast(new RequestEncoder(), new ClientWriteHandler(), new ResponseDecoder(), new RequestFulfiller());

			}
			
			
		});
		
		for (Entry<ChannelOption, Object> channelOption:channelOptions.entrySet()) {
			LOG.debug("Setting {}", channelOption);
			bootstrap.option(channelOption.getKey(), channelOption.getValue());
		}
		
		LOG.debug("Connection object {} created to Host {}", this, host);
		
		
	}

	/**
	 * This should be called by the RequestProcessor
	 * @param request
	 * @return
	 * @throws InterruptedException 
	 */
	public ChannelFuture send(Request request) {
		
		return channel.writeAndFlush(request);
		
	}
	
	public synchronized Connection connect() {
		
		if (channel == null) {
			
			setState(State.CONNECTING, null);
			
			ChannelFuture cFuture = bootstrap.connect(host.getHostInfo().getHostname(), host.getHostInfo().getPort()).addListener(new ChannelFutureListener() {
				
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if (future.isSuccess()) setState(State.CONNECTED, null);
					else setState(State.DISCONNECTED, null);
				}
				
			});
			
			try {
				cFuture.sync();
				
				channel = cFuture.channel();
				LOG.debug("Connected");
				
			} catch (InterruptedException e) {
				
			}

			
		}
		
		return this;
	}
	
	/**
	 * Alter Connection state and notify the Clients' monitors
	 * @param newState
	 * @param cause
	 */
	private void setState(Connection.State newState, Throwable cause) {
		for (ClientMonitor monitor:host.getClient().getMonitors()) {
			
			if (state == State.CONNECTING && newState == State.DISCONNECTED) monitor.connectionFailed(this, cause);
			if (state == State.CONNECTED && newState == State.DISCONNECTED) monitor.connectionLost(this, cause);
			if (newState == State.CREATED) monitor.connectionCreated(this);
			if (newState == State.CONNECTING) monitor.connectionInProgress(this);
			if (newState == State.CONNECTED) monitor.connected(this);
			
		}
		
		state = newState;

	}
	
	public State getState() {
		return state;
	}
	
	public Host getHost() {
		return host;
	}


}