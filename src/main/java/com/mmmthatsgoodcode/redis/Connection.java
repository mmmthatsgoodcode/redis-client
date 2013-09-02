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

import com.mmmthatsgoodcode.redis.client.RedisClientException;
import com.mmmthatsgoodcode.redis.client.pipeline.ClientWriteHandler;
import com.mmmthatsgoodcode.redis.client.pipeline.IdleStateEventHandler;
import com.mmmthatsgoodcode.redis.client.pipeline.CommandEncoder;
import com.mmmthatsgoodcode.redis.client.pipeline.CommandFulfiller;
import com.mmmthatsgoodcode.redis.client.pipeline.CommandLogger;
import com.mmmthatsgoodcode.redis.client.pipeline.ReplyDecoder;
import com.mmmthatsgoodcode.redis.client.pipeline.ReplyLogger;
import com.mmmthatsgoodcode.redis.protocol.Command;
import com.mmmthatsgoodcode.redis.protocol.Reply;
import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand;
import com.mmmthatsgoodcode.redis.protocol.model.PendingReply;

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

	public static final AttributeKey<BlockingQueue<Command>> OUTBOUND = new AttributeKey<BlockingQueue<Command>>("out");
	public static final AttributeKey<Connection> CONNECTION = new AttributeKey<Connection>("connection");

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Connection(Host host, Map<ChannelOption, Object> channelOptions) {
		
		this.host = host;
		
		bootstrap.group(EVENT_LOOP_GROUP);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.handler(new ChannelInitializer<SocketChannel> () {

			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				
				// this will hold the sent commands on the Channel
				ch.attr(OUTBOUND).set(new LinkedBlockingQueue<Command>());
				ch.attr(CONNECTION).set(Connection.this);
				
				if (getHost().getClient().trafficLogging()) ch.pipeline().addLast(new CommandLogger(), new CommandEncoder(), new ClientWriteHandler(), new ReplyLogger(), new ReplyDecoder(), new CommandFulfiller());
				else ch.pipeline().addLast(new CommandEncoder(), new ClientWriteHandler(), new ReplyDecoder(), new CommandFulfiller());
								
			}
			
			
		});
		
		for (Entry<ChannelOption, Object> channelOption:channelOptions.entrySet()) {
			LOG.debug("Setting {}", channelOption);
			bootstrap.option(channelOption.getKey(), channelOption.getValue());
		}
		
		LOG.debug("Connection object {} created to Host {}", this, host);
		
		
	}

	/**
	 * This should be called by the CommandProcessor
	 * @param command
	 * @return
	 * @throws InterruptedException 
	 */
	public <T extends Reply> PendingReply<T> send(final Command<T> command) {
	
		if (getState() == State.CONNECTED) {
			
			channel.writeAndFlush(command).addListener(new ChannelFutureListener() {
				
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					command.getReply().sent(future);
				}
				
			});
			
		}
		
		return command.getReply();
		
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
	 * return pending Commands
	 * @param cause
	 */
	public synchronized void discard(Throwable cause) {
				
		if (state != State.DISCARDED) {
			
			setState(State.DISCARDED, cause);
			
			// finalize remaining commands
			for(Command outbound:channel.attr(OUTBOUND).get()) {
				outbound.getReply().finalize(cause);
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