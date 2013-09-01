package com.mmmthatsgoodcode.redis.protocol.reply;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;

import com.mmmthatsgoodcode.redis.protocol.AbstractReply;

public class StatusReply extends AbstractReply<String> {

	private static final Logger LOG = LoggerFactory.getLogger(StatusReply.class);
	
	public StatusReply(ByteBuf in) {
		super(in);
	}

	/**
	 * Expected format:
	 * +{status reply}CR+LF
	 */
	@Override
	public boolean decode() {
		
		// there is at least one delimiter in the buffer - we can do the decoding
		if (this.in.forEachByte(HAS_DELIMITER) != -1) {
			byte[] statusCode = this.in.readBytes( this.in.forEachByte(HAS_DELIMITER) - this.in.readerIndex() ).array(); // read up to the new line..
			setValue(new String(statusCode));
			LOG.debug("Decoded status reply: \"{}\"", value());
			return true;
		}
		
		return false;
	}	
	

}
