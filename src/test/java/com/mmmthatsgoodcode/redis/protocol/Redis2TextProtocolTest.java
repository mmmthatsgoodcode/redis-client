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
import java.util.Random;

import javax.naming.OperationNotSupportedException;
import javax.print.DocFlavor.READER;

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import com.mmmthatsgoodcode.redis.client.Transaction;
import com.mmmthatsgoodcode.redis.client.UnrecognizedReplyException;
import com.mmmthatsgoodcode.redis.protocol.Redis2TextProtocol.Decoder;
import com.mmmthatsgoodcode.redis.protocol.command.*;
import com.mmmthatsgoodcode.redis.protocol.command.Shutdown;
import com.mmmthatsgoodcode.redis.protocol.reply.*;

public class Redis2TextProtocolTest {

	private final Redis2TextProtocol protocol;
	private final ByteBufAllocator allocator = UnpooledByteBufAllocator.DEFAULT;
	
	public Redis2TextProtocolTest() {
		protocol = new Redis2TextProtocol();		
	}
	
	public Reply fragmentAndDecode(ByteBuf in, Decoder decoder) throws UnrecognizedReplyException {
		
		ByteBuf out = allocator.buffer();
		Random rand = new Random();
		Reply reply = null;
		while(in.isReadable()) {
			int chunkLength = 1;
			if (in.readableBytes() > 1) chunkLength = rand.nextInt(in.readableBytes()-1)+1;
			
			ByteBuf debug = allocator.heapBuffer();
			in.getBytes(in.readerIndex(), debug, chunkLength);
			
			System.out.println("Adding "+chunkLength+" bytes to buffer, or \""+new String(debug.array())+"\"");
			out.writeBytes(in, chunkLength);
			
			reply = decoder.decode( out );
		
		}
		
		in.readerIndex(0);
		return reply;
		
	}
	
	
	@Test
	public void testEncodeExec() throws IOException {
		
		ByteArrayOutputStream execCommandBytes = new ByteArrayOutputStream();
		
		execCommandBytes.write("*1".getBytes(Redis2TextProtocol.ENCODING));
		execCommandBytes.write(Redis2TextProtocol.DELIMITER);		

		execCommandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		execCommandBytes.write(Redis2TextProtocol.DELIMITER);		
				
		execCommandBytes.write("EXEC".getBytes(Redis2TextProtocol.ENCODING));
		execCommandBytes.write(Redis2TextProtocol.DELIMITER);		
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Exec(), out);
		
		byte[] encoded = new byte[out.readableBytes()]; out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, execCommandBytes.toByteArray()));
		
	}
	
	@Test
	public void testEncodeExists() throws IOException {
		
		// create a valid EXISTS command
		ByteArrayOutputStream existsCommandBytes = new ByteArrayOutputStream();
		
		existsCommandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		existsCommandBytes.write(Redis2TextProtocol.DELIMITER);		

		existsCommandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		existsCommandBytes.write(Redis2TextProtocol.DELIMITER);		
				
		existsCommandBytes.write("EXISTS".getBytes(Redis2TextProtocol.ENCODING));
		existsCommandBytes.write(Redis2TextProtocol.DELIMITER);		
		
		existsCommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		existsCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		existsCommandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		existsCommandBytes.write(Redis2TextProtocol.DELIMITER);		
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Exists("SomeKey"), out);
		
		byte[] encoded = new byte[out.readableBytes()]; out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, existsCommandBytes.toByteArray()));		
		
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
		
//		System.out.println(Hex.encodeHex(encoded));
//		System.out.println(Hex.encodeHex(multiCommandBytes.toByteArray()));
		
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
	
	
	/* New Commands Test
	-------------------- */
	
	@Test
	public void testEncodeDecr() throws IOException {
		ByteArrayOutputStream decrCommandBytes = new ByteArrayOutputStream();
		
		decrCommandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("DECR".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Decr("SomeKey"), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, decrCommandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeDecrby() throws IOException {
		ByteArrayOutputStream decrbyCommandBytes = new ByteArrayOutputStream();
		
		decrbyCommandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		decrbyCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrbyCommandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		decrbyCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrbyCommandBytes.write("DECRBY".getBytes(Redis2TextProtocol.ENCODING));
		decrbyCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrbyCommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		decrbyCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrbyCommandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		decrbyCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrbyCommandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		decrbyCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrbyCommandBytes.write("Decrement".getBytes(Redis2TextProtocol.ENCODING));
		decrbyCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Decrby("SomeKey","Decrement".getBytes(Redis2TextProtocol.ENCODING)), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, decrbyCommandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeDiscard() throws IOException {
		ByteArrayOutputStream CommandBytes = new ByteArrayOutputStream();
		
		CommandBytes.write("*1".getBytes(Redis2TextProtocol.ENCODING));
		CommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		CommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		CommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		CommandBytes.write("DISCARD".getBytes(Redis2TextProtocol.ENCODING));
		CommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Discard(), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, CommandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeEcho() throws IOException {
		ByteArrayOutputStream echoCommandBytes = new ByteArrayOutputStream();
		
		echoCommandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		echoCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		echoCommandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		echoCommandBytes.write(Redis2TextProtocol.DELIMITER);		
		
		echoCommandBytes.write("ECHO".getBytes(Redis2TextProtocol.ENCODING));
		echoCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		echoCommandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		echoCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		echoCommandBytes.write("Test".getBytes(Redis2TextProtocol.ENCODING));
		echoCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Echo("Test"), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, echoCommandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeExpire() throws IOException {
		ByteArrayOutputStream CommandBytes = new ByteArrayOutputStream();
		
		CommandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		CommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		CommandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		CommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		CommandBytes.write("EXPIRE".getBytes(Redis2TextProtocol.ENCODING));
		CommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		CommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		CommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		CommandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		CommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		CommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		CommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		CommandBytes.write("Seconds".getBytes(Redis2TextProtocol.ENCODING));
		CommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Expire("SomeKey", "Seconds".getBytes(Redis2TextProtocol.ENCODING)), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, CommandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeExpireat() throws IOException {
		ByteArrayOutputStream CommandBytes = new ByteArrayOutputStream();
		
		CommandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		CommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		CommandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		CommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		CommandBytes.write("EXPIREAT".getBytes(Redis2TextProtocol.ENCODING));
		CommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		CommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		CommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		CommandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		CommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		CommandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		CommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		CommandBytes.write("TimeStamp".getBytes(Redis2TextProtocol.ENCODING));
		CommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Expireat("SomeKey", "TimeStamp".getBytes(Redis2TextProtocol.ENCODING)), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, CommandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeGetbit() throws IOException {
		ByteArrayOutputStream CommandBytes = new ByteArrayOutputStream();
		
		CommandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		CommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		CommandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		CommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		CommandBytes.write("GETBIT".getBytes(Redis2TextProtocol.ENCODING));
		CommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		CommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		CommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		CommandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		CommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		CommandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		CommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		CommandBytes.write("Offset".getBytes(Redis2TextProtocol.ENCODING));
		CommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Getbit("SomeKey", "Offset".getBytes(Redis2TextProtocol.ENCODING)), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, CommandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeGetrange() throws IOException {
		ByteArrayOutputStream CommandBytes = new ByteArrayOutputStream();
		
		CommandBytes.write("*4".getBytes(Redis2TextProtocol.ENCODING));
		CommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		CommandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		CommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		CommandBytes.write("GETRANGE".getBytes(Redis2TextProtocol.ENCODING));
		CommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		CommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		CommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		CommandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		CommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		CommandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		CommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		CommandBytes.write("Start".getBytes(Redis2TextProtocol.ENCODING));
		CommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		CommandBytes.write("$3".getBytes(Redis2TextProtocol.ENCODING));
		CommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		CommandBytes.write("End".getBytes(Redis2TextProtocol.ENCODING));
		CommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Getrange("SomeKey", "Start".getBytes(Redis2TextProtocol.ENCODING), "End".getBytes(Redis2TextProtocol.ENCODING)), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, CommandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeIncr() throws IOException {
		ByteArrayOutputStream decrCommandBytes = new ByteArrayOutputStream();
		
		decrCommandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("INCR".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Incr("SomeKey"), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, decrCommandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeKeys() throws IOException {
		ByteArrayOutputStream decrCommandBytes = new ByteArrayOutputStream();
		
		decrCommandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("KEYS".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$11".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SomePattern".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Keys("SomePattern"), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, decrCommandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeDelOne() throws IOException {
		ByteArrayOutputStream decrCommandBytes = new ByteArrayOutputStream();
		
		decrCommandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$3".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("DEL".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		List<String> temp = new ArrayList<String>();
		temp.add("SomeKey");
		protocol.getEncoder().encode(new Del(temp), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, decrCommandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeDelMany() throws IOException {
		ByteArrayOutputStream decrCommandBytes = new ByteArrayOutputStream();
		
		decrCommandBytes.write("*5".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$3".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("DEL".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("FirstKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SecondKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("ThirdKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("FourthKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		
		List<String> temp = new ArrayList<String>();
		temp.add("FirstKey");
		temp.add("SecondKey");
		temp.add("ThirdKey");
		temp.add("FourthKey");
		
		protocol.getEncoder().encode(new Del(temp), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, decrCommandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeDump() throws IOException {
		ByteArrayOutputStream decrCommandBytes = new ByteArrayOutputStream();
		
		decrCommandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("DUMP".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Dump("SomeKey"), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, decrCommandBytes.toByteArray()));
	}

	@Test
	public void testEncodeGetset() throws IOException {
		ByteArrayOutputStream decrCommandBytes = new ByteArrayOutputStream();

		decrCommandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("GETSET".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$1".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("5".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Getset("SomeKey", "5".getBytes(Redis2TextProtocol.ENCODING)), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, decrCommandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeRename() throws IOException {
		ByteArrayOutputStream decrCommandBytes = new ByteArrayOutputStream();

		decrCommandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("RENAME".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("NewKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Rename("SomeKey", "NewKey"), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, decrCommandBytes.toByteArray()));
	}

	@Test
	public void testEncodeRenamex() throws IOException {
		ByteArrayOutputStream decrCommandBytes = new ByteArrayOutputStream();

		decrCommandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("RENAMEX".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("NewKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Renamex("SomeKey", "NewKey"), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, decrCommandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeRole() throws IOException {
		ByteArrayOutputStream decrCommandBytes = new ByteArrayOutputStream();

		decrCommandBytes.write("*1".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("ROLE".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Role(), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, decrCommandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeRpop() throws IOException {
		ByteArrayOutputStream decrCommandBytes = new ByteArrayOutputStream();

		decrCommandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("RPOP".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Rpop("SomeKey"), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, decrCommandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeRpoplpush() throws IOException {
		ByteArrayOutputStream decrCommandBytes = new ByteArrayOutputStream();

		decrCommandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("RPOPLPUSH".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SourceKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$14".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("DestinationKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Rpoplpush("SourceKey".getBytes(Redis2TextProtocol.ENCODING),"DestinationKey".getBytes(Redis2TextProtocol.ENCODING)), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, decrCommandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeRpushOne() throws IOException {
		ByteArrayOutputStream decrCommandBytes = new ByteArrayOutputStream();
		
		decrCommandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("RPUSH".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SomeValue".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Rpush("SomeKey","SomeValue"), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, decrCommandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeRpushMany() throws IOException {
		ByteArrayOutputStream decrCommandBytes = new ByteArrayOutputStream();
		
		decrCommandBytes.write("*5".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("RPUSH".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$10".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("FirstValue".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$11".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SecondValue".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$10".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("ThirdValue".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Rpush("SomeKey","FirstValue","SecondValue","ThirdValue"), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, decrCommandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeRpushx() throws IOException {
		ByteArrayOutputStream decrCommandBytes = new ByteArrayOutputStream();
		
		decrCommandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("RPUSHX".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SomeValue".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Rpushx("SomeKey","SomeValue"), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, decrCommandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeSaddOne() throws IOException {
		ByteArrayOutputStream decrCommandBytes = new ByteArrayOutputStream();
		
		decrCommandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SADD".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$10".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SomeMember".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Sadd("SomeKey","SomeMember"), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, decrCommandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeSaddMany() throws IOException {
		ByteArrayOutputStream decrCommandBytes = new ByteArrayOutputStream();
		
		decrCommandBytes.write("*5".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SADD".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$11".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("FirstMember".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$12".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SecondMember".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$11".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("ThirdMember".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Sadd("SomeKey","FirstMember","SecondMember","ThirdMember"), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, decrCommandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeScard() throws IOException {
		ByteArrayOutputStream decrCommandBytes = new ByteArrayOutputStream();
		
		decrCommandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SCARD".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Scard("SomeKey"), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, decrCommandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeSdiffOne() throws IOException {
		ByteArrayOutputStream decrCommandBytes = new ByteArrayOutputStream();
		
		decrCommandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SDIFF".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Sdiff("SomeKey"), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, decrCommandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeSdiffMany() throws IOException {
		ByteArrayOutputStream decrCommandBytes = new ByteArrayOutputStream();
		
		decrCommandBytes.write("*5".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SDIFF".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("Key2".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("Key3".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("Key4".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Sdiff("SomeKey","Key2","Key3","Key4"), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, decrCommandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeSdiffstoreOne() throws IOException {
		ByteArrayOutputStream decrCommandBytes = new ByteArrayOutputStream();
		
		decrCommandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$10".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SDIFFSTORE".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$11".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("Destination".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Sdiffstore("Destination","SomeKey"), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, decrCommandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeSdiffstoreMany() throws IOException {
		ByteArrayOutputStream decrCommandBytes = new ByteArrayOutputStream();
		
		decrCommandBytes.write("*6".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$10".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SDIFFSTORE".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$11".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("Destination".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("Key2".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("Key3".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("Key4".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Sdiffstore("Destination","SomeKey","Key2","Key3","Key4"), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, decrCommandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeSelect() throws IOException {
		ByteArrayOutputStream decrCommandBytes = new ByteArrayOutputStream();
		
		decrCommandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SELECT".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("Index".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Select("Index"), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, decrCommandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeSetbit() throws IOException {
		ByteArrayOutputStream decrCommandBytes = new ByteArrayOutputStream();
		
		decrCommandBytes.write("*4".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SETBIT".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("Offset".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("Value".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Setbit("SomeKey","Offset".getBytes(Redis2TextProtocol.ENCODING),"Value".getBytes(Redis2TextProtocol.ENCODING)), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, decrCommandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeSetnx() throws IOException {
		ByteArrayOutputStream decrCommandBytes = new ByteArrayOutputStream();
		
		decrCommandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SETNX".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);

		decrCommandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("Value".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Setnx("SomeKey", "Value".getBytes(Redis2TextProtocol.ENCODING)), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, decrCommandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeSetrange() throws IOException {
		ByteArrayOutputStream decrCommandBytes = new ByteArrayOutputStream();
		
		decrCommandBytes.write("*4".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SETRANGE".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);

		decrCommandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("Offset".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("Value".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Setrange("SomeKey", "Offset".getBytes(Redis2TextProtocol.ENCODING), "Value".getBytes(Redis2TextProtocol.ENCODING)), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, decrCommandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeTime() throws IOException {
		ByteArrayOutputStream decrCommandBytes = new ByteArrayOutputStream();
		
		decrCommandBytes.write("*1".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("TIME".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);

		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Time(), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, decrCommandBytes.toByteArray()));
	}

	@Test
	public void testEncodeTtl() throws IOException {
		ByteArrayOutputStream decrCommandBytes = new ByteArrayOutputStream();
		
		decrCommandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$3".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("TTL".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);

		decrCommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Ttl("SomeKey"), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, decrCommandBytes.toByteArray()));
	}

	@Test
	public void testEncodeType() throws IOException {
		ByteArrayOutputStream decrCommandBytes = new ByteArrayOutputStream();
		
		decrCommandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("TYPE".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);

		decrCommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Type("SomeKey"), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, decrCommandBytes.toByteArray()));
	}

	@Test
	public void testEncodeWatchOne() throws IOException {
		ByteArrayOutputStream decrCommandBytes = new ByteArrayOutputStream();
		
		decrCommandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("WATCH".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);

		decrCommandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Watch("SomeKey"), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, decrCommandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeWatchMany() throws IOException {
		ByteArrayOutputStream decrCommandBytes = new ByteArrayOutputStream();
		
		decrCommandBytes.write("*5".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("WATCH".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);

		decrCommandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("FirstKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);

		decrCommandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("SecondKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);

		decrCommandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("ThirdKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);

		decrCommandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		decrCommandBytes.write("FourthKey".getBytes(Redis2TextProtocol.ENCODING));
		decrCommandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Watch("FirstKey","SecondKey","ThirdKey","FourthKey"), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, decrCommandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeAppend() throws IOException{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("APPEND".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeValue".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Append("SomeKey", "SomeValue".getBytes(Redis2TextProtocol.ENCODING)), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));	
	}

	@Test
	public void testEncodeAuth() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("AUTH".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$12".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomePassword".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Auth("SomePassword"), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeBgsave() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("BGSAVE".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Bgsave(), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeLpop() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("LPOP".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Lpop("SomeKey"), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeBgrewriteaof() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$12".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("BGREWRITEAOF".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Bgrewriteaof(), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeBitcountNoRange() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("BITCOUNT".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("0".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("-1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Bitcount("SomeKey"), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeBitcountWithRange() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("BITCOUNT".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Bitcount("SomeKey","2".getBytes(Redis2TextProtocol.ENCODING), "4".getBytes(Redis2TextProtocol.ENCODING)), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeBitopSingleKey() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("BITOP".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("AND".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("DestKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Bitop("AND".getBytes(Redis2TextProtocol.ENCODING),"DestKey".getBytes(Redis2TextProtocol.ENCODING), "SomeKey".getBytes(Redis2TextProtocol.ENCODING)), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeBitopMultipleKey() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("BITOP".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("AND".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("DestKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("FirstKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SecondKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);

		commandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("ThirdKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);

		commandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("FourthKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode( new Bitop("AND".getBytes(Redis2TextProtocol.ENCODING),"DestKey".getBytes(Redis2TextProtocol.ENCODING), "FirstKey".getBytes(Redis2TextProtocol.ENCODING), "SecondKey".getBytes(Redis2TextProtocol.ENCODING), "ThirdKey".getBytes(Redis2TextProtocol.ENCODING), "FourthKey".getBytes(Redis2TextProtocol.ENCODING) ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeBitposNoRange() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("BITPOS".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("0".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("0".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("-1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode( new Bitpos( "SomeKey", "0".getBytes(Redis2TextProtocol.ENCODING) ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeBitposWithStart() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("BITPOS".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("0".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("-1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode( new Bitpos( "SomeKey", "0".getBytes(Redis2TextProtocol.ENCODING), "1".getBytes(Redis2TextProtocol.ENCODING) ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeBitposWithRange() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("BITPOS".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("0".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode( new Bitpos( "SomeKey", "0".getBytes(Redis2TextProtocol.ENCODING), "1".getBytes(Redis2TextProtocol.ENCODING), "2".getBytes(Redis2TextProtocol.ENCODING) ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeBlpopWithOneKey() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("BLPOP".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode( new Blpop( "5".getBytes(Redis2TextProtocol.ENCODING), "SomeKey" ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeBlpopWithOneKeyNoTimeout() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("BLPOP".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("0".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode( new Blpop( null, "SomeKey" ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeBlpopWithTwoKeys() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("BLPOP".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("OtherKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode( new Blpop( "5".getBytes(Redis2TextProtocol.ENCODING), "SomeKey", "OtherKey" ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeBlpopWithThreeKeys() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("BLPOP".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("OtherKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("LastKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode( new Blpop( "5".getBytes(Redis2TextProtocol.ENCODING), "SomeKey", "OtherKey", "LastKey" ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeBrpopWithOneKey() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("BRPOP".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode( new Brpop( "5".getBytes(Redis2TextProtocol.ENCODING), "SomeKey" ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeBrpopWithOneKeyNoTimeout() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("BRPOP".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("0".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode( new Brpop( null, "SomeKey" ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeBrpopWithTwoKeys() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("BRPOP".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("OtherKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode( new Brpop( "5".getBytes(Redis2TextProtocol.ENCODING), "SomeKey", "OtherKey" ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeBrpopWithThreeKeys() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("BRPOP".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("OtherKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("LastKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode( new Brpop( "5".getBytes(Redis2TextProtocol.ENCODING), "SomeKey", "OtherKey", "LastKey" ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeClientList() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$11".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("CLIENT LIST".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode( new Clientlist( ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}

	@Test
	public void testEncodeClientGetname() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$14".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("CLIENT GETNAME".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode( new Clientgetname( ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}

	@Test
	public void testEncodeCommand() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("COMMAND".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode( new com.mmmthatsgoodcode.redis.protocol.command.Command( ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeCommandCount() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$13".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("COMMAND COUNT".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode( new Commandcount( ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeCommandInfoNoCommand() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$12".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("COMMAND INFO".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode( new Commandinfo( ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeCommandInfoOneCommand() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$12".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("COMMAND INFO".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("GET".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode( new Commandinfo("GET".getBytes(Redis2TextProtocol.ENCODING) ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeCommandInfo() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$12".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("COMMAND INFO".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);

		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("COMMAND".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("GET".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("EVAL".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode( new Commandinfo( "COMMAND".getBytes(Redis2TextProtocol.ENCODING), "GET".getBytes(Redis2TextProtocol.ENCODING), "EVAL".getBytes(Redis2TextProtocol.ENCODING)), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	

	@Test
	public void testEncodeConfigGet() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$10".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("CONFIG GET".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$15".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("*max-*-entries*".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode( new Configget( "*max-*-entries*".getBytes(Redis2TextProtocol.ENCODING)), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeConfigRewrite() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$14".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("CONFIG REWRITE".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode( new Configrewrite( ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeConfigSet() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$10".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("CONFIG SET".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);

		commandBytes.write("$15".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("*max-*-entries*".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("10".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode( new Configset( "*max-*-entries*".getBytes(Redis2TextProtocol.ENCODING), "10".getBytes(Redis2TextProtocol.ENCODING)), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeConfigResetstat() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$16".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("CONFIG RESETSTAT".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);

		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode( new Configresetstat( ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeCommandGetkeys() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$15".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("COMMAND GETKEYS".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("GET".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);

		commandBytes.write("$10".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeString".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		Command command = new Get("SomeString");
		protocol.getEncoder().encode( new Commandgetkeys( command ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeBrpoplpush() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$10".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("BRPOPLPUSH".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$10".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeSource".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$15".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeDestination".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("15".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode( new Brpoplpush("SomeSource".getBytes(Redis2TextProtocol.ENCODING), "SomeDestination".getBytes(Redis2TextProtocol.ENCODING), "15".getBytes(Redis2TextProtocol.ENCODING)), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeDbsize() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("DBSIZE".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode( new Dbsize(), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeFlushall() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("FLUSHALL".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Flushall(), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeFlushdb() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("FLUSHDB".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Flushdb(), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeHdelOne() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("HDEL".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeField".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Hdel("SomeKey", "SomeField".getBytes(Redis2TextProtocol.ENCODING)), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeHdelMany() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("HDEL".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$10".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("FirstField".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$11".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SecondField".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$10".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("ThirdField".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Hdel("SomeKey", "FirstField".getBytes(Redis2TextProtocol.ENCODING), "SecondField".getBytes(Redis2TextProtocol.ENCODING), "ThirdField".getBytes(Redis2TextProtocol.ENCODING)), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeHexists() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("HEXISTS".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeField".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Hexists("SomeKey", "SomeField".getBytes(Redis2TextProtocol.ENCODING)), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeHget() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("HGET".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeField".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Hget("SomeKey", "SomeField".getBytes(Redis2TextProtocol.ENCODING)), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeHgetall() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("HGETALL".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Hgetall("SomeKey"), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeHincrby() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("HINCRBY".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);

		commandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeField".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);

		commandBytes.write("$1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Hincrby("SomeKey", "SomeField".getBytes(Redis2TextProtocol.ENCODING), "4".getBytes(Redis2TextProtocol.ENCODING)), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeHincrbyfloat() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$12".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("HINCRBYFLOAT".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);

		commandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeField".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);

		commandBytes.write("$1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Hincrbyfloat("SomeKey", "SomeField".getBytes(Redis2TextProtocol.ENCODING), "4".getBytes(Redis2TextProtocol.ENCODING)), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}

	@Test
	public void testEncodeHkeys() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("HKEYS".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Hkeys("SomeKey"), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeHlen() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("HLEN".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);

		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Hlen("SomeKey"), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeHmgetOne() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("HMGET".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);

		commandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeField".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Hmget("SomeKey", "SomeField".getBytes(Redis2TextProtocol.ENCODING)), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeHmgetMany() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("HMGET".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);

		commandBytes.write("$10".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("FirstField".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);


		commandBytes.write("$11".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SecondField".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);

		commandBytes.write("$10".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("ThirdField".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);

		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Hmget("SomeKey", "FirstField".getBytes(Redis2TextProtocol.ENCODING), "SecondField".getBytes(Redis2TextProtocol.ENCODING), "ThirdField".getBytes(Redis2TextProtocol.ENCODING)), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeHset() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("HSET".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);

		commandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeField".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);

		commandBytes.write("$1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Hset("SomeKey", "SomeField".getBytes(Redis2TextProtocol.ENCODING), "4".getBytes(Redis2TextProtocol.ENCODING)), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeHsetnx() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("HSETNX".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);

		commandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeField".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);

		commandBytes.write("$1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Hsetnx("SomeKey", "SomeField".getBytes(Redis2TextProtocol.ENCODING), "4".getBytes(Redis2TextProtocol.ENCODING)), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeHvals() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("HVALS".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Hvals("SomeKey"), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeIncrby() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("INCRBY".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);

		commandBytes.write("$1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Incrby("SomeKey", "4".getBytes(Redis2TextProtocol.ENCODING)), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeIncrbyfloat() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$11".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("INCRBYFLOAT".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);

		commandBytes.write("$1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Incrbyfloat("SomeKey", "4".getBytes(Redis2TextProtocol.ENCODING)), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeInfoNoArg() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("INFO".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("default".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Info(), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeInfoWithArg() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("INFO".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$11".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeSection".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Info("SomeSection".getBytes(Redis2TextProtocol.ENCODING)), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeLastsave() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("LASTSAVE".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Lastsave( ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeLindex() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("LINDEX".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeIndex".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Lindex( "SomeKey", "SomeIndex".getBytes(Redis2TextProtocol.ENCODING) ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeLinsert() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("LINSERT".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("BEFORE".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomePivot".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeValue".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Linsert( "SomeKey", "BEFORE".getBytes(Redis2TextProtocol.ENCODING), "SomePivot".getBytes(Redis2TextProtocol.ENCODING), "SomeValue".getBytes(Redis2TextProtocol.ENCODING) ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeLlen() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("LLEN".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Llen( "SomeKey" ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeLpushOneValue() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("LPUSH".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeValue".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Lpush( "SomeKey", "SomeValue".getBytes(Redis2TextProtocol.ENCODING) ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeLpushManyValues() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("LPUSH".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$10".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("FirstValue".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$11".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SecondValue".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$10".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("ThirdValue".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Lpush( "SomeKey", "FirstValue".getBytes(Redis2TextProtocol.ENCODING), "SecondValue".getBytes(Redis2TextProtocol.ENCODING), "ThirdValue".getBytes(Redis2TextProtocol.ENCODING) ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeLpushx() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("LPUSHX".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeValue".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Lpushx( "SomeKey", "SomeValue".getBytes(Redis2TextProtocol.ENCODING) ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeLrange() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("LRANGE".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("Start".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("Stop".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Lrange( "SomeKey", "Start".getBytes(Redis2TextProtocol.ENCODING), "Stop".getBytes(Redis2TextProtocol.ENCODING) ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeLrem() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("LREM".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("Count".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("Value".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Lrem( "SomeKey", "Count".getBytes(Redis2TextProtocol.ENCODING), "Value".getBytes(Redis2TextProtocol.ENCODING) ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeLset() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("LSET".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("Index".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("Value".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Lset( "SomeKey", "Index".getBytes(Redis2TextProtocol.ENCODING), "Value".getBytes(Redis2TextProtocol.ENCODING) ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeLtrim() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("LTRIM".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("Start".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("Stop".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Ltrim( "SomeKey", "Start".getBytes(Redis2TextProtocol.ENCODING), "Stop".getBytes(Redis2TextProtocol.ENCODING) ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeMgetOneKey() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("MGET".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Mget( "SomeKey" ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeMgetManyKey() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("MGET".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("FirstKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SecondKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("ThirdKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("FourthKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		
		List<String> temp = new ArrayList<String>();
		
		protocol.getEncoder().encode(new Mget( "FirstKey","SecondKey","ThirdKey", "FourthKey" ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeMove() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("MOVE".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeDb".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Move( "SomeKey", "SomeDb".getBytes(Redis2TextProtocol.ENCODING) ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodePersist() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("PERSIST".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Persist( "SomeKey" ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodePexpire() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("PEXPIRE".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("1500".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Pexpire( "SomeKey", "1500".getBytes(Redis2TextProtocol.ENCODING) ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodePexpireat() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("PEXPIREAT".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("150000".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Pexpireat( "SomeKey", "150000".getBytes(Redis2TextProtocol.ENCODING) ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodePsetex() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("PSETEX".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("150000".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeValue".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Psetex( "SomeKey", "150000".getBytes(Redis2TextProtocol.ENCODING), "SomeValue".getBytes(Redis2TextProtocol.ENCODING) ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodePttl() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("PTTL".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Pttl( "SomeKey" ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeQuit() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("QUIT".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Quit( ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeRandomkey() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("RANDOMKEY".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Randomkey( ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeSave() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SAVE".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Save( ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeScriptflush() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$12".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SCRIPT FLUSH".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Scriptflush( ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeScriptkill() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$11".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SCRIPT KILL".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Scriptkill( ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeShutdownNosave() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SHUTDOWN".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("NOSAVE".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Shutdown( "NOSAVE".getBytes(Redis2TextProtocol.ENCODING) ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeShutdownSave() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SHUTDOWN".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SAVE".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Shutdown( "SAVE".getBytes(Redis2TextProtocol.ENCODING) ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeSinterOneKey() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SINTER".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Sinter( "SomeKey" ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeSinterManyKeys() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SINTER".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("FirstKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SecondKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("ThirdKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Sinter( "FirstKey", "SecondKey", "ThirdKey" ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeSinterstoreOneKey() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$11".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SINTERSTORE".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$15".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeDestination".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Sinterstore( "SomeDestination", "SomeKey" ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeSinterstoreManyKeys() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$11".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SINTERSTORE".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$15".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeDestination".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("FirstKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SecondKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("ThirdKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Sinterstore( "SomeDestination", "FirstKey", "SecondKey", "ThirdKey" ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeSismember() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SISMEMBER".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$10".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeMember".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Sismember( "SomeKey", "SomeMember".getBytes(Redis2TextProtocol.ENCODING) ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeSmembers() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SMEMBERS".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Smembers( "SomeKey" ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeSmove() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SMOVE".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$10".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeSource".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$15".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeDestination".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$10".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeMember".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Smove( "SomeSource", "SomeDestination", "SomeMember".getBytes(Redis2TextProtocol.ENCODING) ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeSpop() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SPOP".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Spop( "SomeKey" ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeSrandmemberNoCount() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$11".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SRANDMEMBER".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Srandmember( "SomeKey" ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeSrandmemberWithCount() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$11".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SRANDMEMBER".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Srandmember( "SomeKey", "5".getBytes(Redis2TextProtocol.ENCODING) ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeSremOneMember() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SREM".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$10".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeMember".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Srem( "SomeKey", "SomeMember".getBytes(Redis2TextProtocol.ENCODING) ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeSremManyMembers() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*6".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SREM".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$11".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("FirstMember".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$12".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SecondMember".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$11".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("ThirdMember".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$12".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("FourthMember".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Srem( "SomeKey", "FirstMember".getBytes(Redis2TextProtocol.ENCODING), "SecondMember".getBytes(Redis2TextProtocol.ENCODING), "ThirdMember".getBytes(Redis2TextProtocol.ENCODING), "FourthMember".getBytes(Redis2TextProtocol.ENCODING) ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeStrlen() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("STRLEN".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Strlen( "SomeKey" ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeUnwatch() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("UNWATCH".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Unwatch( ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeDebugobject() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$12".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("DEBUG OBJECT".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Debugobject( "SomeKey" ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeDebugsegfault() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$14".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("DEBUG SEGFAULT".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Debugsegfault( ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	
	//TODO change MSet args to MAP
	/*@Test
	public void testEncodeMsetOneMap() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("MSET".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeValue".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new MSet( "SomeKey", "SomeValue" ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}*/
	
	
	/* This test doesn't work since we use a HashMap to store the set[key, value],
	 * But it doesn't matter, since the command is atomic
	 *
	@Test
	public void testEncodeMsetManyMap() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*9".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("MSET".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$10".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeValue1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$10".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeValue2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$10".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeValue3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Mset( "SomeKey1","SomeValue1","SomeKey2","SomeValue2","SomeKey3","SomeValue3" ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}*/
	
	/*@Test
	public void testEncodeMsetnxOneMap() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("MSET".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeValue".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Msetnx( "SomeKey", "SomeValue" ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}*/
	
	
	/* This test doesn't work since we use a HashMap to store the set[key, value],
	 * But it doesn't matter, since the command is atomic
	 *
	@Test
	public void testEncodeMsetnxManyMap() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*9".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("MSET".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$10".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeValue1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$10".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeValue2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$10".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeValue3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Msetnx( "SomeKey1","SomeValue1","SomeKey2","SomeValue2","SomeKey3","SomeValue3" ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}*/
	
	@Test
	public void testEncodePublish() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("PUBLISH".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("Channel".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("Message".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Publish( "Channel".getBytes(Redis2TextProtocol.ENCODING), "Message".getBytes(Redis2TextProtocol.ENCODING) ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeSlowlogNoArg() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SLOWLOG".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$10".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("Subcommand".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Slowlog( "Subcommand".getBytes(Redis2TextProtocol.ENCODING) ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeSlowlogWithArg() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SLOWLOG".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$10".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("Subcommand".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("Argument".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Slowlog( "Subcommand".getBytes(Redis2TextProtocol.ENCODING), "Argument".getBytes(Redis2TextProtocol.ENCODING) ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeSunionOneKey() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SUNION".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Sunion( "SomeKey" ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeSunionManyKey() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$6".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SUNION".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("FirstKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SecondKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("ThirdKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("FourthKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Sunion( "FirstKey", "SecondKey", "ThirdKey", "FourthKey" ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeSunionstoreOneKey() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$11".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SUNIONSTORE".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$15".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeDestination".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Sunionstore( "SomeDestination", "SomeKey" ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeSunionstoreManyKey() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*6".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$11".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SUNIONSTORE".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);

		commandBytes.write("$15".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeDestination".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("FirstKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SecondKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$8".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("ThirdKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$9".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("FourthKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Sunionstore( "SomeDestination", "FirstKey", "SecondKey", "ThirdKey", "FourthKey" ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}

	@Test
	public void testEncodeZcard() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*2".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$5".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("ZCARD".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);

		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Zcard("SomeKey"), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeZremOneMember() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*3".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("ZREM".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$10".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeMember".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Zrem( "SomeKey", "SomeMember".getBytes(Redis2TextProtocol.ENCODING) ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeZremManyMembers() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*6".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$4".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("ZREM".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$7".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SomeKey".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$11".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("FirstMember".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$12".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("SecondMember".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$11".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("ThirdMember".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$12".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("FourthMember".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new Zrem( "SomeKey", "FirstMember".getBytes(Redis2TextProtocol.ENCODING), "SecondMember".getBytes(Redis2TextProtocol.ENCODING), "ThirdMember".getBytes(Redis2TextProtocol.ENCODING), "FourthMember".getBytes(Redis2TextProtocol.ENCODING) ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	
	
	
	
	
	/* XXX
		System.out.println("Encoded :");
		System.out.write(encoded);
		System.out.println("\n commandBytes :");
		System.out.write(commandBytes.toByteArray());
	 * */
	
	/* Replies
	----------- */
	
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

		assertEquals(new BulkReply("Hooray".getBytes()), protocol.getDecoder().decode(allocator.buffer().writeBytes(bulkReplyBytes.toByteArray())));
		
		
	}
	
	@Test
	public void testMultiBulkReply() throws IOException, UnrecognizedReplyException {
		
		// create a valid Multi Bulk reply
		ByteBuf multiBulkReplyBytes = allocator.buffer();
		multiBulkReplyBytes.writeBytes("*2".getBytes(Redis2TextProtocol.ENCODING));
		multiBulkReplyBytes.writeBytes(Redis2TextProtocol.DELIMITER);	
		
		multiBulkReplyBytes.writeBytes("+OK".getBytes(Redis2TextProtocol.ENCODING));
		multiBulkReplyBytes.writeBytes(Redis2TextProtocol.DELIMITER);	
		
		multiBulkReplyBytes.writeBytes("$6".getBytes(Redis2TextProtocol.ENCODING));
		multiBulkReplyBytes.writeBytes(Redis2TextProtocol.DELIMITER);		
		multiBulkReplyBytes.writeBytes("Hooray".getBytes(Redis2TextProtocol.ENCODING));
		multiBulkReplyBytes.writeBytes(Redis2TextProtocol.DELIMITER);		
		
		List<Reply> expectedReplies = new ArrayList<Reply>();
		expectedReplies.add(new StatusReply("OK"));
		expectedReplies.add(new BulkReply("Hooray".getBytes()));
		
		
		
//		System.out.println(protocol.getDecoder().decode(allocator.buffer().writeBytes(multiBulkReplyBytes.toByteArray())));
//		System.out.println(Hex.encodeHex(multiBulkReplyBytes.toByteArray()));
		
		assertEquals(new MultiBulkReply(expectedReplies), fragmentAndDecode( multiBulkReplyBytes, protocol.getDecoder() ));
		
	}

	
}
