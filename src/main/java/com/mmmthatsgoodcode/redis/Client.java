package com.mmmthatsgoodcode.redis;

import com.mmmthatsgoodcode.redis.client.ClientReadHandler;
import com.mmmthatsgoodcode.redis.client.ClientWriteHandler;
import com.mmmthatsgoodcode.redis.client.ResponseDecoder;
import com.mmmthatsgoodcode.redis.command.Get;
import com.mmmthatsgoodcode.redis.command.Ping;

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

	private Bootstrap bootstrap = new Bootstrap();
	
	public Client() {
		
		bootstrap.group(new NioEventLoopGroup());
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.handler(new ChannelInitializer<SocketChannel> () {

			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				
				ch.pipeline().addLast(new ClientWriteHandler(), new ResponseDecoder(), new ClientReadHandler());
				
			}
			
			
		});
		
	}
	
	public ChannelFuture connect(String host, int port) {
		
		return bootstrap.connect(host, port).addListener( new ChannelFutureListener() {
			
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				future.channel().write(new Ping());
			}
		});
		
	}
	
	public ResponseContainer send(Command command) {

		return command.getResponse();
		
	}
	
}
