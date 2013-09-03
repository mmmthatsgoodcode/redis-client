package com.mmmthatsgoodcode.redis.protocol;

import static org.junit.Assert.*;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import com.mmmthatsgoodcode.redis.client.UnrecognizedReplyException;
import com.mmmthatsgoodcode.redis.protocol.reply.ErrorReply;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;
import com.mmmthatsgoodcode.redis.protocol.reply.StatusReply;

public class Redis2TextProtocolTest {

	private final Redis2TextProtocol protocol;
	private final ByteBufAllocator allocator = UnpooledByteBufAllocator.DEFAULT;
	
	public Redis2TextProtocolTest() {
		protocol = new Redis2TextProtocol();		
	}
	
	@Test
	public void testOKStatusReply() throws IOException, IllegalStateException, UnrecognizedReplyException {
		
		// create a valid OK status reply
		ByteArrayOutputStream statusReplyBytes = new ByteArrayOutputStream();
		statusReplyBytes.write("+OK".getBytes(Redis2TextProtocol.ENCODING));
		statusReplyBytes.write(Redis2TextProtocol.DELIMITER);
		
		assertEquals(new StatusReply("OK"), protocol.getDecoder().decode(allocator.buffer().writeBytes(statusReplyBytes.toByteArray())));
		
	}
	
	@Test
	public void testPongStatusReply() throws IOException, IllegalStateException, UnrecognizedReplyException {
		
		// create a valid OK status reply
		ByteArrayOutputStream statusReplyBytes = new ByteArrayOutputStream();
		statusReplyBytes.write("+PONG".getBytes(Redis2TextProtocol.ENCODING));
		statusReplyBytes.write(Redis2TextProtocol.DELIMITER);
		
		assertEquals(new StatusReply("PONG"), protocol.getDecoder().decode(allocator.buffer().writeBytes(statusReplyBytes.toByteArray())));
		
	}	
	
	@Test
	public void testErrorReply() throws IOException, UnrecognizedReplyException {
		
		// create a valid Error status reply
		ByteArrayOutputStream errorReplyBytes = new ByteArrayOutputStream();
		errorReplyBytes.write("-DEVELOPERFAIL You are stupid".getBytes(Redis2TextProtocol.ENCODING));
		errorReplyBytes.write(Redis2TextProtocol.DELIMITER);
				
		assertEquals(new ErrorReply("DEVELOPERFAIL", "You are stupid"), protocol.getDecoder().decode(allocator.buffer().writeBytes(errorReplyBytes.toByteArray())));
		
		
	}
	
	@Test
	public void testIntegerReply() throws IOException, UnrecognizedReplyException {
		
		// create a valid Error status reply
		ByteArrayOutputStream integerReplyBytes = new ByteArrayOutputStream();
		integerReplyBytes.write(":1234".getBytes(Redis2TextProtocol.ENCODING));
		integerReplyBytes.write(Redis2TextProtocol.DELIMITER);
		
		assertEquals(new IntegerReply(1234), protocol.getDecoder().decode(allocator.buffer().writeBytes(integerReplyBytes.toByteArray())));
		
	}
	
}
