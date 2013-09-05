package com.mmmthatsgoodcode.redis.protocol;

import static org.junit.Assert.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import com.mmmthatsgoodcode.redis.client.Transaction;
import com.mmmthatsgoodcode.redis.client.UnrecognizedReplyException;
import com.mmmthatsgoodcode.redis.protocol.Redis2TextProtocol.Decoder;
import com.mmmthatsgoodcode.redis.protocol.command.*;
import com.mmmthatsgoodcode.redis.protocol.reply.*;

public class Redis2TextProtocolTest {

	private final Redis2TextProtocol protocol;
	private final ByteBufAllocator allocator = UnpooledByteBufAllocator.DEFAULT;
	
	public Redis2TextProtocolTest() {
		protocol = new Redis2TextProtocol();		
	}
	
	@Test
	public void testEncodeTransaction() throws IOException, OperationNotSupportedException {
		
		ByteArrayOutputStream transactionBytes = new ByteArrayOutputStream();

		transactionBytes.write("*1".getBytes(Redis2TextProtocol.ENCODING));
		transactionBytes.write(Redis2TextProtocol.DELIMITER);		

		transactionBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		transactionBytes.write(Redis2TextProtocol.DELIMITER);		
				
		transactionBytes.write("MULTI".getBytes(Redis2TextProtocol.ENCODING));
		transactionBytes.write(Redis2TextProtocol.DELIMITER);		
		
		transactionBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		transactionBytes.write(Redis2TextProtocol.DELIMITER);		

		transactionBytes.write("$3".getBytes(Redis2TextProtocol.ENCODING));
		transactionBytes.write(Redis2TextProtocol.DELIMITER);		
				
		transactionBytes.write("SET".getBytes(Redis2TextProtocol.ENCODING));
		transactionBytes.write(Redis2TextProtocol.DELIMITER);	
		
		transactionBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		transactionBytes.write(Redis2TextProtocol.DELIMITER);		
				
		transactionBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		transactionBytes.write(Redis2TextProtocol.DELIMITER);	
		
		transactionBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		transactionBytes.write(Redis2TextProtocol.DELIMITER);		
				
		transactionBytes.write("SomeValue".getBytes(Redis2TextProtocol.ENCODING));
		transactionBytes.write(Redis2TextProtocol.DELIMITER);
		
		transactionBytes.write("*1".getBytes(Redis2TextProtocol.ENCODING));
		transactionBytes.write(Redis2TextProtocol.DELIMITER);		

		transactionBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		transactionBytes.write(Redis2TextProtocol.DELIMITER);		
				
		transactionBytes.write("PING".getBytes(Redis2TextProtocol.ENCODING));
		transactionBytes.write(Redis2TextProtocol.DELIMITER);

		transactionBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		transactionBytes.write(Redis2TextProtocol.DELIMITER);		

		transactionBytes.write("$3".getBytes(Redis2TextProtocol.ENCODING));
		transactionBytes.write(Redis2TextProtocol.DELIMITER);		
				
		transactionBytes.write("GET".getBytes(Redis2TextProtocol.ENCODING));
		transactionBytes.write(Redis2TextProtocol.DELIMITER);	
		
		transactionBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		transactionBytes.write(Redis2TextProtocol.DELIMITER);		
				
		transactionBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		transactionBytes.write(Redis2TextProtocol.DELIMITER);	
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Transaction().add(new Set("SomeKey", "SomeValue".getBytes())).add(new Ping()).add(new Get("SomeKey")), out);

		byte[] encoded = new byte[out.readableBytes()]; out.readBytes(encoded);

//		System.out.println(Hex.encodeHex(encoded));
//		System.out.println(Hex.encodeHex(transactionBytes.toByteArray()));
		
		assertTrue(Arrays.equals(encoded, transactionBytes.toByteArray()));

		
	}
	
	@Test
	public void testEncodeMulti() throws IOException {
		
		// create a valid PING command
		ByteArrayOutputStream multiCommandBytes = new ByteArrayOutputStream();
		
		multiCommandBytes.write("*1".getBytes(Redis2TextProtocol.ENCODING));
		multiCommandBytes.write(Redis2TextProtocol.DELIMITER);		

		multiCommandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		multiCommandBytes.write(Redis2TextProtocol.DELIMITER);		
				
		multiCommandBytes.write("MULTI".getBytes(Redis2TextProtocol.ENCODING));
		multiCommandBytes.write(Redis2TextProtocol.DELIMITER);		
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Multi(), out);
		
		byte[] encoded = new byte[out.readableBytes()]; out.readBytes(encoded);
		
		System.out.println(Hex.encodeHex(encoded));
		System.out.println(Hex.encodeHex(multiCommandBytes.toByteArray()));
		
		assertTrue(Arrays.equals(encoded, multiCommandBytes.toByteArray()));
		
	}	
	
	@Test
	public void testEncodePing() throws IOException {
		
		// create a valid PING command
		ByteArrayOutputStream pingCommandBytes = new ByteArrayOutputStream();
		
		pingCommandBytes.write("*1".getBytes(Redis2TextProtocol.ENCODING));
		pingCommandBytes.write(Redis2TextProtocol.DELIMITER);		

		pingCommandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		pingCommandBytes.write(Redis2TextProtocol.DELIMITER);		
				
		pingCommandBytes.write("PING".getBytes(Redis2TextProtocol.ENCODING));
		pingCommandBytes.write(Redis2TextProtocol.DELIMITER);		
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Ping(), out);
		
		byte[] encoded = new byte[out.readableBytes()]; out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, pingCommandBytes.toByteArray()));
		
	}	
	
	@Test
	public void testEncodeGet() throws IOException {
		
		// create a valid GET command
		ByteArrayOutputStream getCommandBytes = new ByteArrayOutputStream();
		
		getCommandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		getCommandBytes.write(Redis2TextProtocol.DELIMITER);		

		getCommandBytes.write("$3".getBytes(Redis2TextProtocol.ENCODING));
		getCommandBytes.write(Redis2TextProtocol.DELIMITER);		
				
		getCommandBytes.write("GET".getBytes(Redis2TextProtocol.ENCODING));
		getCommandBytes.write(Redis2TextProtocol.DELIMITER);	
		
		getCommandBytes.write("$3".getBytes(Redis2TextProtocol.ENCODING));
		getCommandBytes.write(Redis2TextProtocol.DELIMITER);		
				
		getCommandBytes.write("Foo".getBytes(Redis2TextProtocol.ENCODING));
		getCommandBytes.write(Redis2TextProtocol.DELIMITER);	
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Get("Foo"), out);
		
		byte[] encoded = new byte[out.readableBytes()]; out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, getCommandBytes.toByteArray()));
		
	}
	
	@Test
	public void testEncodeSet() throws IOException {
		
		// create a valid GET command
		ByteArrayOutputStream setCommandBytes = new ByteArrayOutputStream();
		
		setCommandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		setCommandBytes.write(Redis2TextProtocol.DELIMITER);		

		setCommandBytes.write("$3".getBytes(Redis2TextProtocol.ENCODING));
		setCommandBytes.write(Redis2TextProtocol.DELIMITER);		
				
		setCommandBytes.write("SET".getBytes(Redis2TextProtocol.ENCODING));
		setCommandBytes.write(Redis2TextProtocol.DELIMITER);	
		
		setCommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		setCommandBytes.write(Redis2TextProtocol.DELIMITER);		
				
		setCommandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		setCommandBytes.write(Redis2TextProtocol.DELIMITER);	
		
		setCommandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		setCommandBytes.write(Redis2TextProtocol.DELIMITER);		
				
		setCommandBytes.write("SomeValue".getBytes(Redis2TextProtocol.ENCODING));
		setCommandBytes.write(Redis2TextProtocol.DELIMITER);	
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Set("SomeKey", "SomeValue".getBytes()), out);
		
		byte[] encoded = new byte[out.readableBytes()]; out.readBytes(encoded);
		
//		System.out.println(Hex.encodeHex(encoded));
//		System.out.println(Hex.encodeHex(setCommandBytes.toByteArray()));
		
		assertTrue(Arrays.equals(encoded, setCommandBytes.toByteArray()));
		
	}
	
	@Test
	public void testEncodeSetex() throws IOException {
		
		// create a valid GET command
		ByteArrayOutputStream setexCommandBytes = new ByteArrayOutputStream();
		
		setexCommandBytes.write("*4".getBytes(Redis2TextProtocol.ENCODING));
		setexCommandBytes.write(Redis2TextProtocol.DELIMITER);		

		setexCommandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		setexCommandBytes.write(Redis2TextProtocol.DELIMITER);		
				
		setexCommandBytes.write("SETEX".getBytes(Redis2TextProtocol.ENCODING));
		setexCommandBytes.write(Redis2TextProtocol.DELIMITER);	
		
		setexCommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		setexCommandBytes.write(Redis2TextProtocol.DELIMITER);		
				
		setexCommandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		setexCommandBytes.write(Redis2TextProtocol.DELIMITER);	
		
		setexCommandBytes.write("$2".getBytes(Redis2TextProtocol.ENCODING));
		setexCommandBytes.write(Redis2TextProtocol.DELIMITER);		
				
		setexCommandBytes.write("99".getBytes(Redis2TextProtocol.ENCODING));
		setexCommandBytes.write(Redis2TextProtocol.DELIMITER);	
		
		setexCommandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		setexCommandBytes.write(Redis2TextProtocol.DELIMITER);		
				
		setexCommandBytes.write("SomeValue".getBytes(Redis2TextProtocol.ENCODING));
		setexCommandBytes.write(Redis2TextProtocol.DELIMITER);	
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Setex("SomeKey", "SomeValue".getBytes(), 99), out);
		
		byte[] encoded = new byte[out.readableBytes()]; out.readBytes(encoded);
		
//		System.out.println(Hex.encodeHex(encoded));
//		System.out.println(Hex.encodeHex(setCommandBytes.toByteArray()));
		
		assertTrue(Arrays.equals(encoded, setexCommandBytes.toByteArray()));
		
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
		
		
		
		System.out.println(protocol.getDecoder().decode(allocator.buffer().writeBytes(multiBulkReplyBytes.toByteArray())));
		System.out.println(Hex.encodeHex(multiBulkReplyBytes.toByteArray()));
		
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
