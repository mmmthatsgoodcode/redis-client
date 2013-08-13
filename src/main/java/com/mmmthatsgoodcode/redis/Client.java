package com.mmmthatsgoodcode.redis;

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

	public enum State { CONNECTING, CONNECTED, DISCONNECTED }
	protected volatile State state = State.CONNECTING;
	private Channel channel;
	
	private Bootstrap bootstrap = new Bootstrap();
	
	public Client() {
		
		bootstrap.group(new NioEventLoopGroup());
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.handler(new ChannelInitializer<SocketChannel> () {

			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				
				ch.pipeline().addLast(new RequestEncoder(), new ClientWriteHandler(), new ResponseDecoder());
				
			}
			
			
		});
		
	}
	
	public Client connect(String host, int port) throws InterruptedException {
		
		channel = bootstrap.connect(host, port).sync().channel();
		return this;
		
	}
	
	public PendingResponse send(Request request) {
		
		channel.writeAndFlush(request);
		return request.getResponse();
		
	}
	
}
