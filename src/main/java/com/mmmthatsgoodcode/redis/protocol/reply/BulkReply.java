package com.mmmthatsgoodcode.redis.protocol.reply;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;
import io.netty.buffer.UnpooledByteBufAllocator;

import com.mmmthatsgoodcode.redis.protocol.model.AbstractReply;

/**
 * This should contain a single, arbitary-length incoming parameter as specified by ${length}DELIMITER
 * @author aszerdahelyi
 *
 */
public class BulkReply extends AbstractReply<String> {

	private static final Logger LOG = LoggerFactory.getLogger(BulkReply.class);

	public BulkReply(String value) {
		setValue(value);
	}

}
