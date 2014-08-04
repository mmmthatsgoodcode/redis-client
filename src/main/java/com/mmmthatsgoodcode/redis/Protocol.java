package com.mmmthatsgoodcode.redis;

import javax.naming.OperationNotSupportedException;

import com.mmmthatsgoodcode.redis.client.Transaction;
import com.mmmthatsgoodcode.redis.client.UnrecognizedReplyException;
import com.mmmthatsgoodcode.redis.protocol.Command;
import com.mmmthatsgoodcode.redis.protocol.Reply;
import com.mmmthatsgoodcode.redis.protocol.command.*;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public interface Protocol {
	
	public enum ReplyType { BULK, ERROR, INTEGER, MULTI_BULK, STATUS, UNKNOWN }
	public enum CommandType { APPEND, AUTH, BGSAVE, DECR, DECRBY, DEL, DISCARD, DUMP, ECHO, EXEC,
							EXISTS, EXPIRE, EXPIREAT, GET, GETBIT, GETRANGE, GETSET,
							INCR, KEYS, MULTI, PING, RENAME, RENAMEX, ROLE, RPOP, RPOPLPUSH, RPUSH, RPUSHX, SADD, SCARD,
							SDIFF, SDIFFSTORE, SELECT, SET, SETBIT, SETEX,
							SETNX, SETRANGE, TIME, TTL, TYPE, WATCH
							}
	
	public ByteBufAllocator getByteBufAllocator();
	
	public interface Encoder {

		//TODO fill Commands
		public void encode(Append command, ByteBuf out);
		public void encode(Auth command, ByteBuf out);
		public void encode(Bgsave command, ByteBuf out);
		public void encode(Decr command, ByteBuf out);
		public void encode(Decrby command, ByteBuf out);
		public void encode(Del command, ByteBuf out);
		public void encode(Discard command, ByteBuf out);
		public void encode(Dump command, ByteBuf out);
		public void encode(Echo command, ByteBuf out);
		public void encode(Exec command, ByteBuf out);
		public void encode(Exists command, ByteBuf out);
		public void encode(Expire command, ByteBuf out);
		public void encode(Expireat command, ByteBuf out);
		public void encode(Get command, ByteBuf out);
		public void encode(Getbit command, ByteBuf out);
		public void encode(Getrange command, ByteBuf out);
		public void encode(Getset command, ByteBuf out);
		public void encode(Incr command, ByteBuf out);
		public void encode(Keys command, ByteBuf out);
		public void encode(Multi command, ByteBuf out);
		public void encode(Ping command, ByteBuf out);
		public void encode(Rename command, ByteBuf out);
		public void encode(Renamex command, ByteBuf out);
		public void encode(Role command, ByteBuf out);
		public void encode(Rpop command, ByteBuf out);
		public void encode(Rpoplpush command, ByteBuf out);
		public void encode(Rpush command, ByteBuf out);
		public void encode(Rpushx command, ByteBuf out);
		public void encode(Sadd command, ByteBuf out);
		public void encode(Scard command, ByteBuf out);
		public void encode(Sdiff command, ByteBuf out);
		public void encode(Sdiffstore command, ByteBuf out);
		public void encode(Select command, ByteBuf out);
		public void encode(Set command, ByteBuf out);
		public void encode(Setbit command, ByteBuf out);
		public void encode(Setex command, ByteBuf out);
		public void encode(Setnx command, ByteBuf out);
		public void encode(Setrange command, ByteBuf out);
		public void encode(Time command, ByteBuf out);
		public void encode(Ttl command, ByteBuf out);
		public void encode(Type command, ByteBuf out);
		public void encode(Watch command, ByteBuf out);
		public void encode(Command command, ByteBuf out) throws OperationNotSupportedException;		
		public void encode(Transaction command, ByteBuf out) throws OperationNotSupportedException;
		
	}
	
	public interface Decoder {

		public Reply decode(ByteBuf in) throws UnrecognizedReplyException;
		public ReplyType replyType();
		
	}
	

	public Protocol.Encoder getEncoder();
	public Protocol.Decoder getDecoder();

	
}
