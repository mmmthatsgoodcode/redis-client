package com.mmmthatsgoodcode.redis;

import java.nio.charset.Charset;

import javax.naming.OperationNotSupportedException;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.mmmthatsgoodcode.redis.client.Transaction;
import com.mmmthatsgoodcode.redis.client.UnrecognizedReplyException;
import com.mmmthatsgoodcode.redis.protocol.Command;
import com.mmmthatsgoodcode.redis.protocol.Reply;
import com.mmmthatsgoodcode.redis.protocol.command.*;
import com.mmmthatsgoodcode.redis.protocol.reply.*;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufProcessor;
import io.netty.buffer.PooledByteBufAllocator;

public interface Protocol {
	
	public enum ReplyType { BULK, ERROR, INTEGER, MULTI_BULK, STATUS, UNKNOWN }
	public enum CommandType { GET, SET, SETEX, EXEC, EXISTS, MSET, MULTI, PING, SETNX, WATCH, BGREWRITEAOF, BGSAVE, CLIENTGETNAME, CLIENTLIST, CLUSTERSLOTS, COMMAND, COMMANDCOUNT, CONFIGRESETSTAT, CONFIGREWRITE, DBSIZE,DEBUGSEGFAULT, DISCARD, FLUSHALL, FLUSHDB, LASTSAVE, QUIT, RANDOMKEY, ROLE, SAVE, SCRIPTFLUSH, SCRIPTKILL, TIME, UNWATCH }
	
	public ByteBufAllocator getByteBufAllocator();
	
	public interface Encoder {

		public void encode(Exec command, ByteBuf out);
		public void encode(Exists command, ByteBuf out);
		public void encode(Get command, ByteBuf out);
		public void encode(MSet command, ByteBuf out);
		public void encode(Multi command, ByteBuf out);
		public void encode(Ping command, ByteBuf out);
		public void encode(Set command, ByteBuf out);
		public void encode(Setex command, ByteBuf out);
		public void encode(Setnx command, ByteBuf out);
		public void encode(Watch command, ByteBuf out);
		public void encode(Bgrewriteaof command, ByteBuf out);
		public void encode(Bgsave command, ByteBuf out);
		public void encode(ClientGetname command, ByteBuf out);
		public void encode(ClientList command, ByteBuf out);
		public void encode(ClusterSlots command, ByteBuf out);
		public void encode(com.mmmthatsgoodcode.redis.protocol.command.Command command, ByteBuf out);
		public void encode(CommandCount command, ByteBuf out);
		public void encode(ConfigResetstat command, ByteBuf out);
		public void encode(ConfigRewrite command, ByteBuf out);
		public void encode(Dbsize command, ByteBuf out);
		public void encode(DebugSegfault command, ByteBuf out);
		public void encode(Discard command, ByteBuf out);
		public void encode(Flushall command, ByteBuf out);
		public void encode(Flushdb command, ByteBuf out);
		public void encode(Lastsave command, ByteBuf out);
		public void encode(Quit command, ByteBuf out);
		public void encode(Randomkey command, ByteBuf out);
		public void encode(Role command, ByteBuf out);
		public void encode(Save command, ByteBuf out);
		public void encode(ScriptFlush command, ByteBuf out);
		public void encode(ScriptKill command, ByteBuf out);
		public void encode(Time command, ByteBuf out);
		public void encode(Unwatch command, ByteBuf out);
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
