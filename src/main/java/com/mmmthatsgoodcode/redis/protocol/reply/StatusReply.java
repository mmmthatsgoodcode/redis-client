package com.mmmthatsgoodcode.redis.protocol.reply;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;

import com.mmmthatsgoodcode.redis.protocol.model.AbstractReply;

public class StatusReply extends AbstractReply<String> {

	private static final Logger LOG = LoggerFactory.getLogger(StatusReply.class);
	
	public StatusReply(String statusCode) {
		setValue(statusCode);
	}
	
	public boolean equals(Object object) {
		if (!(object instanceof StatusReply)) return false;
		
		StatusReply other = (StatusReply) object;
		return other.value().equals(value());
	}


}
