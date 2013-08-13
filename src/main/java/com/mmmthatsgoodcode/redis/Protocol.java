package com.mmmthatsgoodcode.redis;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufProcessor;
import io.netty.buffer.PooledByteBufAllocator;

public abstract class Protocol {
	
	protected static final Charset ENCODING = Charset.forName("UTF-8");
	public static final byte[] DELIMITER = "\r\n".getBytes(ENCODING);
	protected static ByteBufAllocator byteBufAllocator = new PooledByteBufAllocator();
	
	
	
}
