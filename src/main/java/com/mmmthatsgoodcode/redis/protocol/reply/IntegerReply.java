package com.mmmthatsgoodcode.redis.protocol.reply;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;

import com.mmmthatsgoodcode.redis.protocol.model.AbstractReply;

public class IntegerReply extends AbstractReply<Integer> {

	public IntegerReply(Integer value) {
		setValue(value);
	}

	public boolean equals(Object object) {
		if (!(object instanceof IntegerReply)) return false;
		
		IntegerReply other = (IntegerReply) object;
		return other.value().equals(value());
		
	}
	
}
