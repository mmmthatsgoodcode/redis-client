package com.mmmthatsgoodcode.redis.client.pipeline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmmthatsgoodcode.redis.Protocol;
import com.mmmthatsgoodcode.redis.Protocol.Decoder;
import com.mmmthatsgoodcode.redis.client.UnrecognizedReplyException;
import com.mmmthatsgoodcode.redis.protocol.Reply;
import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand;
import com.mmmthatsgoodcode.redis.protocol.model.AbstractReply;
import com.mmmthatsgoodcode.redis.protocol.reply.BulkReply;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.AttributeKey;

public class ReplyDecoder extends ByteToMessageDecoder {

	private final static Logger LOG = LoggerFactory.getLogger(ReplyDecoder.class);

	private List<Reply> replies = new ArrayList<Reply>();
	private final Protocol protocol;
	private Decoder currentDecoder = null;
	
	public ReplyDecoder(Protocol protocol) {
		this.protocol = protocol;
	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		
		LOG.debug("Processing {} readable bytes", in.readableBytes());
		// if there are readable bytes on the buffer - there should be..
		while (in.readableBytes() > 0) {
			LOG.debug("Reading from index {} ( {} readable )", in.readerIndex(), in.readableBytes());
			// first, find out what kind of reply this is ( if the first byte is already available on the buffer - it should be )
			if (currentDecoder == null) {
				currentDecoder = protocol.getDecoder();
//				LOG.debug("Inferred next reply in buffer to be: {}", currentDecoder);
			}

			// decode contents from the buffer
//			try {
				Reply currentReply = currentDecoder.decode(in);
				if (currentReply != null) {
					// decoder finished
					replies.add(currentReply);
					if (LOG.isDebugEnabled() && currentReply.value() != null && currentReply instanceof BulkReply) LOG.debug("Decoded reply: {}", new String((byte[])currentReply.value()));
					else LOG.debug("Decoded reply: {}", currentReply.value());
					currentDecoder = protocol.getDecoder();
	
					LOG.debug("Buffer after decode: {}/{}", in.readerIndex(), in.readableBytes());
					// see if we are done completely decoding the buffer
					if (in.readableBytes() == 0) {
						LOG.debug("End of buffer");
						
						out.add(replies);
						return;
	
					}
					
					// there are still bytes in the buffer after decode, that are not CRLF
					LOG.debug("Still {} bytes buffered to go..", in.readableBytes());
					
				} else {
	
					LOG.debug("Insufficient bytes in buffer to decode a reply ( {} readable bytes )", in.readableBytes());
					// bytes in buffer were not enough to decode a reply, continue..
					return;
				
				}
			
//			} catch (UnrecognizedReplyException e) {
//				
//				return;
//				
//			}

		
		}
		
		return;
		
	}

}
