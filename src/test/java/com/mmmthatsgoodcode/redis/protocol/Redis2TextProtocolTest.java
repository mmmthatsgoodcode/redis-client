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

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import com.mmmthatsgoodcode.redis.client.Transaction;
import com.mmmthatsgoodcode.redis.client.UnrecognizedReplyException;
import com.mmmthatsgoodcode.redis.protocol.Redis2TextProtocol.Decoder;
import com.mmmthatsgoodcode.redis.protocol.command.*;
import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand;
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
	public void testEncodeClientGetname() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$14".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("CLIENT GETNAME".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode( new ClientGetname( ), out);
		
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
		protocol.getEncoder().encode( new ClientList( ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
	}
	
	@Test
	public void testEncodeClusterSlots() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$13".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("CLUSTER SLOTS".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode( new ClusterSlots( ), out);
		
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
		protocol.getEncoder().encode( new CommandCount( ), out);
		
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
		protocol.getEncoder().encode( new ConfigResetstat( ), out);
		
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
		protocol.getEncoder().encode( new ConfigRewrite( ), out);
		
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
	public void testEncodeDebugsegfault() throws IOException	{
		ByteArrayOutputStream commandBytes = new ByteArrayOutputStream();
		
		commandBytes.write("*1".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("$14".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		commandBytes.write("DEBUG SEGFAULT".getBytes(Redis2TextProtocol.ENCODING));
		commandBytes.write(Redis2TextProtocol.DELIMITER);
		
		ByteBuf out = allocator.heapBuffer();
		protocol.getEncoder().encode(new DebugSegfault( ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
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
		protocol.getEncoder().encode(new ScriptFlush( ), out);
		
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
		protocol.getEncoder().encode(new ScriptKill( ), out);
		
		byte[] encoded = new byte[out.readableBytes()];
		out.readBytes(encoded);
		assertTrue(Arrays.equals(encoded, commandBytes.toByteArray()));
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
