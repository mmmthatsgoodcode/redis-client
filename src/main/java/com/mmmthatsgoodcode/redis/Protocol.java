package com.mmmthatsgoodcode.redis;

import javax.naming.OperationNotSupportedException;

import com.mmmthatsgoodcode.redis.client.Transaction;
import com.mmmthatsgoodcode.redis.client.UnrecognizedReplyException;
import com.mmmthatsgoodcode.redis.protocol.Reply;
import com.mmmthatsgoodcode.redis.protocol.command.*;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public interface Protocol {
	
	public enum ReplyType { BULK, ERROR, INTEGER, MULTI_BULK, STATUS, UNKNOWN }
	public enum CommandType { APPEND, AUTH, BGREWRITEAOF, BGSAVE, BITCOUNT, BITOP, BITPOS, BLPOP, BRPOP, BRPOPLPUSH,
							CLIENTGETNAME, CLIENTLIST, COMMAND, COMMANDCOUNT,COMMANDGETKEYS, COMMANDINFO, CONFIGGET,
							CONFIGRESETSTAT, CONFIGREWRITE, CONFIGSET, DBSIZE, DEBUGOBJECT, DEBUGSEGFAULT, DECR, DECRBY, DEL, DISCARD, DUMP, ECHO,
							EXEC, EXISTS, EXPIRE, EXPIREAT, FLUSHALL, FLUSHDB, GET, GETBIT, GETRANGE, GETSET, HDEL,
							HEXISTS, HGET, HGETALL, HINCRBY, HINCRBYFLOAT, HKEYS, HLEN, HMGET, HSET, HSETNX, HVALS,
							INCR, INCRBY, INCRBYFLOAT, INFO, KEYS, LASTSAVE, LINDEX, LINSERT, LLEN, LPOP, LPUSH,
							LPUSHX, LRANGE, LREM, LSET, LTRIM, MGET, MONITOR, MOVE, MSET, MSETNX, MULTI, PERSIST, PEXPIRE, PEXPIREAT, PING,
							PSETEX, PTTL, PUBLISH, QUIT, RANDOMKEY, RENAME, RENAMEX, ROLE, RPOP, RPOPLPUSH, RPUSH, RPUSHX, SADD,
							SAVE, SCARD, SCRIPTFLUSH, SCRIPTKILL, SDIFF, SDIFFSTORE, SELECT, SET, SETBIT, SETEX, SETNX,
							SETRANGE, SHUTDOWN, SINTER, SINTERSTORE, SISMEMBER, SLOWLOG, SMEMBERS, SMOVE, SPOP, SRANDMEMBER,
							SREM, STRLEN, SUNION, SUNIONSTORE, SYNC, TIME, TTL, TYPE, UNWATCH, WATCH, ZCARD, ZRANK, ZREM
							}
	
	public ByteBufAllocator getByteBufAllocator();
	
	public interface Encoder {

		//TODO fill Commands
		public void encode(Append command, ByteBuf out);
		public void encode(Auth command, ByteBuf out);
		public void encode(Bgrewriteaof command, ByteBuf out);
		public void encode(Bgsave command, ByteBuf out);
		public void encode(Bitcount command, ByteBuf out);
		public void encode(Bitop command, ByteBuf out);
		public void encode(Bitpos command, ByteBuf out);
		public void encode(Blpop command, ByteBuf out);
		public void encode(Brpop command, ByteBuf out);
		public void encode(Brpoplpush command, ByteBuf out);
		public void encode(Clientgetname command, ByteBuf out);
		public void encode(Clientlist command, ByteBuf out);
		public void encode(com.mmmthatsgoodcode.redis.protocol.command.Command command, ByteBuf out);
		public void encode(Commandcount command, ByteBuf out);
		public void encode(Commandgetkeys command, ByteBuf out);
		public void encode(Commandinfo command, ByteBuf out);
		public void encode(Configget command, ByteBuf out);
		public void encode(Configresetstat command, ByteBuf out);
		public void encode(Configrewrite command, ByteBuf out);
		public void encode(Configset command, ByteBuf out);
		public void encode(Dbsize command, ByteBuf out);
		public void encode(Debugobject command, ByteBuf out);
		public void encode(Debugsegfault command, ByteBuf out);
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
		public void encode(Flushall command, ByteBuf out);
		public void encode(Flushdb command, ByteBuf out);
		public void encode(Get command, ByteBuf out);
		public void encode(Getbit command, ByteBuf out);
		public void encode(Getrange command, ByteBuf out);
		public void encode(Getset command, ByteBuf out);
		public void encode(Hdel command, ByteBuf out);
		public void encode(Hexists command, ByteBuf out);
		public void encode(Hget command, ByteBuf out);
		public void encode(Hgetall command, ByteBuf out);
		public void encode(Hincrby command, ByteBuf out);
		public void encode(Hincrbyfloat command, ByteBuf out);
		public void encode(Hkeys command, ByteBuf out);
		public void encode(Hlen command, ByteBuf out);
		public void encode(Hmget command, ByteBuf out);
		public void encode(Hset command, ByteBuf out);
		public void encode(Hsetnx command, ByteBuf out);
		public void encode(Hvals command, ByteBuf out);
		public void encode(Incr command, ByteBuf out);
		public void encode(Incrby command, ByteBuf out);
		public void encode(Incrbyfloat command, ByteBuf out);
		public void encode(Info command, ByteBuf out);
		public void encode(Keys command, ByteBuf out);
		public void encode(Lastsave command, ByteBuf out);
		public void encode(Lindex command, ByteBuf out);
		public void encode(Linsert command, ByteBuf out);
		public void encode(Llen command, ByteBuf out);
		public void encode(Lpop command, ByteBuf out);
		public void encode(Lpush command, ByteBuf out);
		public void encode(Lpushx command, ByteBuf out);
		public void encode(Lrange command, ByteBuf out);
		public void encode(Lrem command, ByteBuf out);
		public void encode(Lset command, ByteBuf out);
		public void encode(Ltrim command, ByteBuf out);
		public void encode(Mget command, ByteBuf out);
		public void encode(Move command, ByteBuf out);
		public void encode(Mset command, ByteBuf out);
		public void encode(Msetnx command, ByteBuf out);
		public void encode(Multi command, ByteBuf out);		
		public void encode(Persist command, ByteBuf out);
		public void encode(Pexpire command, ByteBuf out);
		public void encode(Pexpireat command, ByteBuf out);
		public void encode(Ping command, ByteBuf out);
		public void encode(Psetex command, ByteBuf out);
		public void encode(Pttl command, ByteBuf out);
		public void encode(Publish command, ByteBuf out);
		public void encode(Quit command, ByteBuf out);
		public void encode(Randomkey command, ByteBuf out);
		public void encode(Rename command, ByteBuf out);
		public void encode(Renamex command, ByteBuf out);
		public void encode(Role command, ByteBuf out);
		public void encode(Rpop command, ByteBuf out);
		public void encode(Rpoplpush command, ByteBuf out);
		public void encode(Rpush command, ByteBuf out);
		public void encode(Rpushx command, ByteBuf out);
		public void encode(Sadd command, ByteBuf out);
		public void encode(Save command, ByteBuf out);
		public void encode(Scard command, ByteBuf out);
		public void encode(Scriptflush command, ByteBuf out);
		public void encode(Scriptkill command, ByteBuf out);
		public void encode(Sdiff command, ByteBuf out);
		public void encode(Sdiffstore command, ByteBuf out);
		public void encode(Select command, ByteBuf out);
		public void encode(Set command, ByteBuf out);
		public void encode(Setbit command, ByteBuf out);
		public void encode(Setex command, ByteBuf out);
		public void encode(Setnx command, ByteBuf out);
		public void encode(Setrange command, ByteBuf out);
		public void encode(Shutdown command, ByteBuf out);
		public void encode(Sinter command, ByteBuf out);
		public void encode(Sinterstore command, ByteBuf out);
		public void encode(Sismember command, ByteBuf out);
		public void encode(Slowlog command, ByteBuf out);
		public void encode(Smembers command, ByteBuf out);
		public void encode(Smove command, ByteBuf out);
		public void encode(Spop command, ByteBuf out);
		public void encode(Srandmember command, ByteBuf out);
		public void encode(Srem command, ByteBuf out);
		public void encode(Strlen command, ByteBuf out);
		public void encode(Sunion command, ByteBuf out);
		public void encode(Sunionstore command, ByteBuf out);
		//public void encode(Sync command, ByteBuf out);
		public void encode(Time command, ByteBuf out);
		public void encode(Ttl command, ByteBuf out);
		public void encode(Type command, ByteBuf out);
		public void encode(Unwatch command, ByteBuf out);
		public void encode(Watch command, ByteBuf out);
		public void encode(Zcard command, ByteBuf out);
		//public void encode(Zrank command, ByteBuf out);
		public void encode(Zrem command, ByteBuf out);
		public void encode(com.mmmthatsgoodcode.redis.protocol.Command command, ByteBuf out) throws OperationNotSupportedException;		
		public void encode(Transaction command, ByteBuf out) throws OperationNotSupportedException;
		
	}
	
	public interface Decoder {

		public Reply decode(ByteBuf in) throws UnrecognizedReplyException;
		public ReplyType replyType();
		
	}
	

	public Protocol.Encoder getEncoder();
	public Protocol.Decoder getDecoder();

	
}
