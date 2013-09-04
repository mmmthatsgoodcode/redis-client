package com.mmmthatsgoodcode.redis.protocol;

import static org.junit.Assert.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.mmmthatsgoodcode.redis.client.UnrecognizedReplyException;
import com.mmmthatsgoodcode.redis.protocol.Redis2TextProtocol.Decoder;
import com.mmmthatsgoodcode.redis.protocol.reply.BulkReply;
import com.mmmthatsgoodcode.redis.protocol.reply.ErrorReply;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;
import com.mmmthatsgoodcode.redis.protocol.reply.MultiBulkReply;
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
	
	@Test
	public void testBulkReply() throws IOException, UnrecognizedReplyException {
		
		// create a valid Bulk reply
		ByteArrayOutputStream bulkReplyBytes = new ByteArrayOutputStream();
		bulkReplyBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		bulkReplyBytes.write(Redis2TextProtocol.DELIMITER);		
		bulkReplyBytes.write("Hooray".getBytes(Redis2TextProtocol.ENCODING));
		bulkReplyBytes.write(Redis2TextProtocol.DELIMITER);		

		assertEquals(new BulkReply("Hooray"), protocol.getDecoder().decode(allocator.buffer().writeBytes(bulkReplyBytes.toByteArray())));
		
		
	}
	
	@Test
	public void testMultiBulkReply() throws IOException, UnrecognizedReplyException {
		
		// create a valid Multi Bulk reply
		ByteArrayOutputStream multiBulkReplyBytes = new ByteArrayOutputStream();
		multiBulkReplyBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		multiBulkReplyBytes.write(Redis2TextProtocol.DELIMITER);	
		
		multiBulkReplyBytes.write("+OK".getBytes(Redis2TextProtocol.ENCODING));
		multiBulkReplyBytes.write(Redis2TextProtocol.DELIMITER);	
		
		multiBulkReplyBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		multiBulkReplyBytes.write(Redis2TextProtocol.DELIMITER);		
		multiBulkReplyBytes.write("Hooray".getBytes(Redis2TextProtocol.ENCODING));
		multiBulkReplyBytes.write(Redis2TextProtocol.DELIMITER);		
		
		List<Reply> expectedReplies = new ArrayList<Reply>();
		expectedReplies.add(new StatusReply("OK"));
		expectedReplies.add(new BulkReply("Hooray"));
		
		assertEquals(new MultiBulkReply(expectedReplies), protocol.getDecoder().decode(allocator.buffer().writeBytes(multiBulkReplyBytes.toByteArray())));
		
	}
	
	@Test
	public void testMultiPartMultiBulkReply() throws IOException, UnrecognizedReplyException {

		Decoder decoder = protocol.getDecoder();
		ByteBuf incoming = allocator.buffer();
		
		// create a valid Multi Bulk reply .. in 3 parts, to simulate TCP packets

		incoming.writeBytes("*2".getBytes(Redis2TextProtocol.ENCODING));
		incoming.writeBytes(Redis2TextProtocol.DELIMITER);	
		
		// decode 1st pass
		decoder.decode(incoming);
		
		incoming.writeBytes("+O".getBytes(Redis2TextProtocol.ENCODING));

		// decode 2nd pass
		decoder.decode(incoming);

		incoming.writeBytes("K".getBytes(Redis2TextProtocol.ENCODING));
		incoming.writeBytes(Redis2TextProtocol.DELIMITER);	
		
		incoming.writeBytes("$6".getBytes(Redis2TextProtocol.ENCODING));
		incoming.writeBytes(Redis2TextProtocol.DELIMITER);	
		
		// decode 3rd pass
		decoder.decode(incoming);

		incoming.writeBytes("Hooray".getBytes(Redis2TextProtocol.ENCODING));
		incoming.writeBytes(Redis2TextProtocol.DELIMITER);		
		

		List<Reply> expectedReplies = new ArrayList<Reply>();
		expectedReplies.add(new StatusReply("OK"));
		expectedReplies.add(new BulkReply("Hooray"));

		// decode final pass, assert
		assertEquals(new MultiBulkReply(expectedReplies), decoder.decode(incoming));

		
	}
	
}
