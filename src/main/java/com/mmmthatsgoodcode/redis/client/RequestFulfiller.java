package com.mmmthatsgoodcode.redis.client;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.redis.Connection;
import com.mmmthatsgoodcode.redis.protocol.Request;
import com.mmmthatsgoodcode.redis.protocol.Response;

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
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	
    		
    		if (msg instanceof List) {
    			List list = (List) msg;
    			for(Object obj:list) {
    				
    				Response response = (Response) obj;
    				
    				Request request = ctx.channel().attr(Connection.OUTBOUND).get().poll();
    	    		if (request != null) {
    					request.getResponse().finalize(response);
    					LOG.debug("Finalized request {}", request);
    	    			
    	    		}
    			}
    			
    		}
    		
    		
    		
    	
    }
	
}
