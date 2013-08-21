package com.mmmthatsgoodcode.redis;

import java.util.ArrayList;
import java.util.List;
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
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.redis.client.ClientWriteHandler;
import com.mmmthatsgoodcode.redis.client.IdleStateEventHandler;
import com.mmmthatsgoodcode.redis.client.RedisClientException;
import com.mmmthatsgoodcode.redis.client.RequestEncoder;
import com.mmmthatsgoodcode.redis.client.RequestFulfiller;
import com.mmmthatsgoodcode.redis.client.RequestLogger;
import com.mmmthatsgoodcode.redis.client.ResponseDecoder;
import com.mmmthatsgoodcode.redis.client.ResponseLogger;
import com.mmmthatsgoodcode.redis.protocol.PendingResponse;
import com.mmmthatsgoodcode.redis.protocol.AbstractRequest;
import com.mmmthatsgoodcode.redis.protocol.Request;
import com.mmmthatsgoodcode.redis.protocol.Response;

/**
 * Represents a single connection to a RedisHost.
 * @author andras
 *
 */
public class Connection  {
	
	
	public enum State { CREATED, CONNECTING, CONNECTED, DISCONNECTED, DISCARDED }

	protected final static EventLoopGroup EVENT_LOOP_GROUP = new NioEventLoopGroup();
	protected volatile Connection.State state = State.CREATED;		
	private Channel channel = null;
	private Bootstrap bootstrap = new Bootstrap();
	private final Host host;
	private static final Logger LOG = LoggerFactory.getLogger(Connection.class);

	public static final AttributeKey<BlockingQueue<Request>> OUTBOUND = new AttributeKey<BlockingQueue<Request>>("out");
	public static final AttributeKey<Connection> CONNECTION = new AttributeKey<Connection>("connection");

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Connection(Host host, Map<ChannelOption, Object> channelOptions) {
		
		this.host = host;
		
		bootstrap.group(EVENT_LOOP_GROUP);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.handler(new ChannelInitializer<SocketChannel> () {

			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				
				// this will hold the sent requests on the Channel
				ch.attr(OUTBOUND).set(new LinkedBlockingQueue<Request>());
				ch.attr(CONNECTION).set(Connection.this);
				
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
	public <T extends Response> PendingResponse<T> send(final Request<T> request) {
	
		if (getState() == State.CONNECTED) {
			
			channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
				
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					request.getResponse().sent(future);
				}
				
			});
			
		}
		
		return request.getResponse();
		
	}
	
	public synchronized Connection connect() {
		
		if (channel == null) {
			
			setState(State.CONNECTING, null);
			
			ChannelFuture cFuture = bootstrap.connect(host.getHostInfo().getHostname(), host.getHostInfo().getPort()).addListener(new ChannelFutureListener() {
				
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if (future.isSuccess()) setState(State.CONNECTED, null);
					else setState(State.DISCARDED, null);
					
				}
				
			});
			
			channel = cFuture.channel();
			channel.closeFuture().addListener(new ChannelFutureListener() {
				
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					
					Connection.this.discard(new RedisClientException());
					
				}
				
			});
							
			
		}
		
		return this;
	}
	
	/**
	 * Clean up and shut down the channel, remove this connection from RedisHost,
	 * return pending Requests
	 * @param cause
	 */
	public synchronized void discard(Throwable cause) {
				
		if (state != State.DISCARDED) {
			
			setState(State.DISCARDED, cause);
			
			// finalize remaining requests
			for(Request outbound:channel.attr(OUTBOUND).get()) {
				outbound.getResponse().finalize(cause);
			}
			
			// clear attributes
			channel.attr(OUTBOUND).remove();
			channel.attr(CONNECTION).remove();
			channel.close();
			channel = null;
			
		}
				
	}
	
	/**
	 * Alter Connection state and notify the Clients' monitors
	 * @param newState
	 * @param cause
	 */
	private void setState(Connection.State newState, Throwable cause) {
		
		for (ClientMonitor monitor:host.getClient().getMonitors()) {
			
			LOG.debug("Notifying {} of new State {}", monitor, newState);
			
			if (state == State.CONNECTING && newState == State.DISCONNECTED) monitor.connectionFailed(this, cause);
			if (state == State.CONNECTED && newState == State.DISCONNECTED) monitor.connectionLost(this, cause);
			if (newState == State.CREATED) monitor.connectionCreated(this);
			if (newState == State.CONNECTING) monitor.connectionInProgress(this);
			if (newState == State.CONNECTED) monitor.connected(this);
			if (newState == State.DISCARDED) monitor.connectionDiscarded(this, cause);
			
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