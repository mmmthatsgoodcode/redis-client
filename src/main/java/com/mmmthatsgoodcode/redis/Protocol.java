package com.mmmthatsgoodcode.redis;

import java.nio.charset.Charset;

import javax.naming.OperationNotSupportedException;

import com.mmmthatsgoodcode.redis.client.Transaction;
import com.mmmthatsgoodcode.redis.client.UnrecognizedReplyException;
import com.mmmthatsgoodcode.redis.protocol.Reply;
import com.mmmthatsgoodcode.redis.protocol.command.*;
import com.mmmthatsgoodcode.redis.protocol.reply.*;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufProcessor;
import io.netty.buffer.PooledByteBufAllocator;

public interface Protocol {
	
	public enum ReplyType { BULK, ERROR, INTEGER, MULTI_BULK, STATUS, UNKNOWN }
	public enum CommandType { GET, SET, EXEC, EXISTS, MULTI, PING, SETNX, WATCH }
	
	public ByteBufAllocator getByteBufAllocator();
	
	public interface Encoder {

		public ByteBuf encode(Exec command);
		public ByteBuf encode(Exists command);
		public ByteBuf encode(Get command);
		public ByteBuf encode(Multi command);
		public ByteBuf encode(Ping command);
		public ByteBuf encode(Set command);
		public ByteBuf encode(Setex command);
		public ByteBuf encode(Setnx command);
		public ByteBuf encode(Watch command);		
		public ByteBuf encodeTransaction(Transaction command) throws OperationNotSupportedException;
		
	}
	
	public interface Decoder {

		public Reply decode(ByteBuf in) throws UnrecognizedReplyException;

	}
	

	public Protocol.Encoder getEncoder();
	public Protocol.Decoder getDecoder();

	
}
