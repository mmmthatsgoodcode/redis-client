package com.mmmthatsgoodcode.redis.client;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.redis.Connection;
import com.mmmthatsgoodcode.redis.protocol.Request;
import com.mmmthatsgoodcode.redis.protocol.AbstractResponse;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * Pulls the next Outbound request from the outbound request queue of this channel and fulfills it with this Response
 * @author andras
 *
 */
public class RequestFulfiller extends ChannelInboundHandlerAdapter {
	
	private static final Logger LOG = LoggerFactory.getLogger(RequestFulfiller.class);
	
	/**
	 * This is waiting for ResponseDecoder to pass a list of complete Responses down to it
	 */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	
    		
    		if (msg instanceof List) {
    			
    			List list = (List) msg;
    			LOG.debug("Finalizing {} requests", ((List) msg).size());
    			for(Object obj:list) {
    				
    				AbstractResponse response = (AbstractResponse) obj;
    				
    				// the assumption here is, Redis is sending responses in the order of requests sent
    				Request request = ctx.channel().attr(Connection.OUTBOUND).get().poll();
    	    		if (request != null) {
    					request.getResponse().finalize(response);
    					LOG.debug("Finalized request {}", request);
    	    			
    	    		}
    			}
    			
    			if (ctx.channel().attr(Connection.OUTBOUND).get().size() > 0) LOG.debug("{} requests still pending", ctx.channel().attr(Connection.OUTBOUND).get().size());
    			list.clear();
    			
    		}
    	
    }
    
    @Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	
    	LOG.warn("?!");
    	Connection connection = ctx.channel().attr(Connection.CONNECTION).get();
    	if (connection != null) connection.discard(cause);
    	
    }
	
}
