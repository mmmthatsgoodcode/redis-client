package com.mmmthatsgoodcode.redis.protocol.reply;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;

import com.mmmthatsgoodcode.redis.protocol.model.AbstractReply;

public class IntegerReply extends AbstractReply<Integer> {

	public IntegerReply(Integer value) {
		setValue(value);
	}
	
}
