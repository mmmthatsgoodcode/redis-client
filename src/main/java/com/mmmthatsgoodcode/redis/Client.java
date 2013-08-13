package com.mmmthatsgoodcode.redis;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import com.mmmthatsgoodcode.redis.client.ClientWriteHandler;
import com.mmmthatsgoodcode.redis.client.RequestEncoder;
import com.mmmthatsgoodcode.redis.client.ResponseDecoder;
import com.mmmthatsgoodcode.redis.protocol.PendingResponse;
import com.mmmthatsgoodcode.redis.protocol.Request;
import com.mmmthatsgoodcode.redis.protocol.Response;
import com.mmmthatsgoodcode.redis.protocol.ResponseContainer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {

	public enum State { CREATED, CONNECTING, CONNECTED, DISCONNECTED }
	protected List<ClientMonitor> monitors = new ArrayList<ClientMonitor>();
	protected volatile State state = State.CREATED;
	private final String host;
	private final int port;
	private Channel channel;
	
	private Bootstrap bootstrap = new Bootstrap();
	
	public Client(String host, int port) {	
		
		this.host = host;
		this.port = port;
		
		bootstrap.group(new NioEventLoopGroup());
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 100);
		bootstrap.handler(new ChannelInitializer<SocketChannel> () {

			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				
				ch.pipeline().addLast(new RequestEncoder(), new ClientWriteHandler(), new ResponseDecoder());
				
			}
			
			
		});
		
	}
	
	public Client(String host, int port, List<ClientMonitor> monitors) {
		this(host, port);
		
		this.monitors = monitors;
		setState(State.CREATED, null);
		
	}
	
	public Client connect() {
		
		setState(State.CONNECTING, null);
		
		channel = bootstrap.connect(host, port).addListener(new ChannelFutureListener() {
			
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) setState(State.CONNECTED, null);
				else setState(State.DISCONNECTED, null);
			}
			
		}).channel();
		
		return this;
		
	}
	
	/**
	 * 1) disconnect channel
	 * 2) connect channel
	 * @return
	 */
	public Client reconnect() {
		
		// mark client as disconnected
		state = State.DISCONNECTED; 

		// disconnect the channel
		channel.disconnect().addListener(new ChannelFutureListener() {
			
			
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				
				setState(State.CONNECTING, null);

				// connect the channel
				InetSocketAddress remoteAddress = new InetSocketAddress(host, port);
				future.channel().connect(remoteAddress).addListener(new ChannelFutureListener() {
					
					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						 
						
						if (future.isSuccess()) {
							// great, it worked
							setState(State.CONNECTED, null);
						} else {
							// :-(
							setState(State.DISCONNECTED, future.cause());
						}
						
					}
					
				});
				
			}
		});
		
		return this;
	}
	
	public String toString() {
		return "("+host+":"+port+")#"+hashCode();
	}
	
	private void setState(State newState, Throwable cause) {
		for (ClientMonitor monitor:monitors) {
			
			if (state == State.CONNECTING && newState == State.DISCONNECTED) monitor.clientConnectionFailed(this, cause);
			if (state == State.CONNECTED && newState == State.DISCONNECTED) monitor.clientDisconnected(this, cause);
			if (newState == State.CREATED) monitor.clientCreated(this);
			if (newState == State.CONNECTING) monitor.clientConnecting(this);
			if (newState == State.CONNECTED) monitor.clientConnected(this);
			
		}
		
		state = newState;

	}
	
	public PendingResponse send(Request request) {
		
		channel.writeAndFlush(request);
		return request.getResponse();
		
	}
	
}
