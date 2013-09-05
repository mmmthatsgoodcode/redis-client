package com.mmmthatsgoodcode.redis.protocol.reply;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;

import com.mmmthatsgoodcode.redis.protocol.Reply;
import com.mmmthatsgoodcode.redis.protocol.model.AbstractReply;

public class MultiBulkReply extends AbstractReply<List<Reply>> {
	
	private static final Logger LOG = LoggerFactory.getLogger(MultiBulkReply.class);
	
	public MultiBulkReply(List<Reply> replies) {
		setValue(replies);
	}
	
	
}
