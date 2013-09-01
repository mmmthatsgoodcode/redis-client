package com.mmmthatsgoodcode.redis.client.pipeline;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.redis.Connection;
import com.mmmthatsgoodcode.redis.protocol.Command;
import com.mmmthatsgoodcode.redis.protocol.AbstractReply;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * Pulls the next Outbound command from the outbound commands queue of this channel and fulfills it with this Reply
 * @author andras
 *
 */
public class CommandFulfiller extends ChannelInboundHandlerAdapter {
	
	private static final Logger LOG = LoggerFactory.getLogger(CommandFulfiller.class);
	
	/**
	 * This is waiting for ReplyDecoder to pass a list of complete Replies down to it
	 */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	
    		
    		if (msg instanceof List) {
    			
    			List list = (List) msg;
    			LOG.debug("Finalizing {} commands", ((List) msg).size());
    			for(Object obj:list) {
    				
    				AbstractReply reply = (AbstractReply) obj;
    				
    				// the assumption here is, Redis is sending replies in the order of commands sent
    				Command command = ctx.channel().attr(Connection.OUTBOUND).get().poll();
    	    		if (command != null) {
    					command.getReply().finalize(reply);
    					LOG.debug("Finalized command {}", command);
    	    			
    	    		}
    			}
    			
    			if (ctx.channel().attr(Connection.OUTBOUND).get().size() > 0) LOG.debug("{} commands still pending", ctx.channel().attr(Connection.OUTBOUND).get().size());
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
