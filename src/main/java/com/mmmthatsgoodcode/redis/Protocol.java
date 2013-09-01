package com.mmmthatsgoodcode.redis;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufProcessor;
import io.netty.buffer.PooledByteBufAllocator;

public abstract class Protocol {
	
	// a ByteBufProcessor that finds delimiters
	public static final ByteBufProcessor HAS_DELIMITER = ByteBufProcessor.FIND_CRLF;
	
	// Character encoding
	public static final Charset ENCODING = Charset.forName("UTF-8");
	
	// Command delimiter
	public static final byte[] DELIMITER = "\r\n".getBytes(ENCODING);
	
	// Allocator to grab buffers from
	protected static ByteBufAllocator byteBufAllocator = new PooledByteBufAllocator();
	
	
	
}
