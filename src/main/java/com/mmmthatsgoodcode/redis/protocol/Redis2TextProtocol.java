package com.mmmthatsgoodcode.redis.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufProcessor;
import io.netty.buffer.PooledByteBufAllocator;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.OperationNotSupportedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.mmmthatsgoodcode.redis.Protocol;
import com.mmmthatsgoodcode.redis.client.Transaction;
import com.mmmthatsgoodcode.redis.client.UnrecognizedReplyException;
import com.mmmthatsgoodcode.redis.protocol.Command;
import com.mmmthatsgoodcode.redis.protocol.command.*;
import com.mmmthatsgoodcode.redis.protocol.model.AbstractReply.ReplyHintBytes;
import com.mmmthatsgoodcode.redis.protocol.reply.BulkReply;
import com.mmmthatsgoodcode.redis.protocol.reply.ErrorReply;
import com.mmmthatsgoodcode.redis.protocol.reply.IntegerReply;
import com.mmmthatsgoodcode.redis.protocol.reply.MultiBulkReply;
import com.mmmthatsgoodcode.redis.protocol.reply.StatusReply;

public class Redis2TextProtocol implements Protocol {
	
	private static final Logger LOG = LoggerFactory.getLogger(Redis2TextProtocol.class);

	public class Encoder implements Protocol.Encoder {

		private class EncodeHelper {
			
			private final ByteBuf out;
			
			public EncodeHelper(ByteBuf out) {
				this.out = out;
			}
			
			/**
			 * Add argument count to outgoing reply
			 * @param argc
			 * @return
			 */
			public EncodeHelper addArgc(int argc) {
				this.out.writeByte(ARGC_BEGIN);
				this.out.writeBytes(String.valueOf(argc).getBytes(ENCODING));
				this.out.writeBytes(DELIMITER);
				return this;
			}
			
			/**
			 * Add argument to outgoing reply
			 * @param argument
			 * @return
			 */
			public EncodeHelper addArg(byte[] argument) {
				this.out.writeByte(ARG_LENGTH_BEGIN);
				this.out.writeBytes(String.valueOf(argument.length).getBytes(ENCODING));
				this.out.writeBytes(DELIMITER);
				this.out.writeBytes(argument);
				this.out.writeBytes(DELIMITER);
				return this;
			}
			
			public ByteBuf buffer() {
				return this.out;
			}
		}

		@Override
		public void encode(Append command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(3);
			helper.addArg(commandNames.get(CommandType.APPEND));
			helper.addArg(command.getKey().getBytes(ENCODING));
			helper.addArg(command.getValue());
		}

		@Override
		public void encode(Auth command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(2);
			helper.addArg(commandNames.get(CommandType.AUTH));
			helper.addArg(command.getKey().getBytes(ENCODING));
		}

		@Override
		public void encode(Bgrewriteaof command, ByteBuf out) {
			encodeNoArgCommand(out, commandNames.get(CommandType.BGREWRITEAOF));
		}

		@Override
		public void encode(Bgsave command, ByteBuf out) {
			encodeNoArgCommand(out, commandNames.get(CommandType.BGSAVE));
		}

		@Override
		public void encode(Bitcount command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(4);
			helper.addArg(commandNames.get(CommandType.BITCOUNT));
			helper.addArg(command.getKey().getBytes(ENCODING));
			helper.addArg(command.getStart());
			helper.addArg(command.getEnd());
		}

		@Override
		public void encode(Bitop command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(3+command.getKeys().size());
			helper.addArg(commandNames.get(CommandType.BITOP));
			helper.addArg(command.getOperation());
			helper.addArg(command.getDestinationKey());
			
			for(byte[] key : command.getKeys()){
				helper.addArg(key);
			}
		}

		@Override
		public void encode(Bitpos command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(5);
			helper.addArg(commandNames.get(CommandType.BITPOS));
			helper.addArg(command.getKey().getBytes(ENCODING));
			helper.addArg(command.getBitSearched());
			helper.addArg(command.getStartingBit());
			helper.addArg(command.getEndingBit());
		}

		@Override
		public void encode(Blpop command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(3+command.getKeys().size());
			helper.addArg(commandNames.get(CommandType.BLPOP));
			helper.addArg(command.getKey().getBytes(ENCODING));
			
			for(String key: command.getKeys()){
				helper.addArg(key.getBytes(ENCODING));
			}
			helper.addArg(command.getTimeout());
		}

		@Override
		public void encode(Brpop command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(3+command.getKeys().size());
			helper.addArg(commandNames.get(CommandType.BRPOP));
			helper.addArg(command.getKey().getBytes(ENCODING));
			
			for(String key: command.getKeys()){
				helper.addArg(key.getBytes(ENCODING));
			}
			helper.addArg(command.getTimeout());
			
		}

		@Override
		public void encode(Brpoplpush command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(3);
			helper.addArg(commandNames.get(CommandType.BRPOPLPUSH));
			helper.addArg(command.getSource());
			helper.addArg(command.getDestination());
			helper.addArg(command.getTimeout());
		}

		@Override
		public void encode(Clientgetname command, ByteBuf out) {
			encodeNoArgCommand(out, commandNames.get(CommandType.CLIENTGETNAME));
		}

		@Override
		public void encode(Clientlist command, ByteBuf out) {
			encodeNoArgCommand(out, commandNames.get(CommandType.CLIENTLIST));
		}

		@Override
		public void encode(com.mmmthatsgoodcode.redis.protocol.command.Command command, ByteBuf out) {
			encodeNoArgCommand(out, commandNames.get(CommandType.COMMAND));
		}

		@Override
		public void encode(Commandcount command, ByteBuf out) {
			encodeNoArgCommand(out, commandNames.get(CommandType.COMMANDCOUNT));
		}

		@Override
		public void encode(Commandgetkeys command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(2);
			helper.addArg(commandNames.get(CommandType.COMMANDGETKEYS));
			try {
				encode(command.getCommand(), out);
			} catch (OperationNotSupportedException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void encode(Commandinfo command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(1+command.getCommandList().size());
			helper.addArg(commandNames.get(CommandType.COMMANDINFO));
			
			for(byte[] commandName : command.getCommandList()){
				helper.addArg(commandName);
			}
		}

		@Override
		public void encode(Configget command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(2);
			helper.addArg(commandNames.get(CommandType.CONFIGGET));
			helper.addArg(command.getParameter());
		}

		@Override
		public void encode(Configresetstat command, ByteBuf out) {
			encodeNoArgCommand(out, commandNames.get(CommandType.CONFIGRESETSTAT));
		}

		@Override
		public void encode(Configrewrite command, ByteBuf out) {
			encodeNoArgCommand(out, commandNames.get(CommandType.CONFIGREWRITE));
		}

		@Override
		public void encode(Configset command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(3);
			helper.addArg(commandNames.get(CommandType.CONFIGSET));
			helper.addArg(command.getParameter());
			helper.addArg(command.getValue());
		}

		@Override
		public void encode(Dbsize command, ByteBuf out) {
			encodeNoArgCommand(out, commandNames.get(CommandType.DBSIZE));
		}

		@Override
		public void encode(Decr command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(2);
			helper.addArg(commandNames.get(CommandType.DECR));
			helper.addArg(command.getKey().getBytes(ENCODING));
		}
		
		@Override
		public void encode(Decrby command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(3);
			helper.addArg(commandNames.get(CommandType.DECRBY));
			helper.addArg(command.getKey().getBytes(ENCODING));
			helper.addArg(command.getDecrement());
		}
		
		@Override
		public void encode(Del command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(1+command.getKeys().size());
			helper.addArg(commandNames.get(CommandType.DEL));
			
			for(String key : command.getKeys()){
				helper.addArg(key.getBytes(ENCODING));
			}
		}
		
		@Override
		public void encode(Discard command, ByteBuf out) {
			encodeNoArgCommand(out, commandNames.get(CommandType.DISCARD));
		}
		
		@Override
		public void encode(Dump command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(2);
			helper.addArg(commandNames.get(CommandType.DUMP));
			helper.addArg(command.getKey().getBytes(ENCODING));
		}
		
		@Override
		public void encode(Echo command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(2);
			helper.addArg(commandNames.get(CommandType.ECHO));
			helper.addArg(command.getKey().getBytes(ENCODING));
		}
		
		@Override
		public void encode(Exec command, ByteBuf out) {
			encodeNoArgCommand(out, commandNames.get(CommandType.EXEC));
		}

		@Override
		public void encode(Exists command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(2);
			helper.addArg(commandNames.get(CommandType.EXISTS));
			helper.addArg(command.getKey().getBytes(ENCODING));
		}
		
		@Override
		public void encode(Expire command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(3);
			helper.addArg(commandNames.get(CommandType.EXPIRE));
			helper.addArg(command.getKey().getBytes(ENCODING));
			helper.addArg(command.getSeconds());
		}
		
		@Override
		public void encode(Expireat command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(3);
			helper.addArg(commandNames.get(CommandType.EXPIREAT));
			helper.addArg(command.getKey().getBytes(ENCODING));
			helper.addArg(command.getTimeStamp());
		}

		@Override
		public void encode(Flushall command, ByteBuf out) {
			encodeNoArgCommand(out, commandNames.get(CommandType.FLUSHALL));
		}

		@Override
		public void encode(Flushdb command, ByteBuf out) {
			encodeNoArgCommand(out, commandNames.get(CommandType.FLUSHDB));
		}
		
		@Override
		public void encode(Get command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(2);
			helper.addArg(commandNames.get(CommandType.GET));
			helper.addArg(command.getKey().getBytes(ENCODING));
		}

		@Override
		public void encode(Getbit command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(3);
			helper.addArg(commandNames.get(CommandType.GETBIT));
			helper.addArg(command.getKey().getBytes(ENCODING));
			helper.addArg(command.getValue());
		}
		
		@Override
		public void encode(Getrange command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(4);
			helper.addArg(commandNames.get(CommandType.GETRANGE));
			helper.addArg(command.getKey().getBytes(ENCODING));
			helper.addArg(command.getStart());
			helper.addArg(command.getEnd());	
		}

		@Override
		public void encode(Getset command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(3);
			helper.addArg(commandNames.get(CommandType.GETSET));
			helper.addArg(command.getKey().getBytes(ENCODING));
			helper.addArg(command.getValue());
		}

		@Override
		public void encode(Hdel command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(2+command.getFieldsList().size());
			helper.addArg(commandNames.get(CommandType.HDEL));
			helper.addArg(command.getKey().getBytes(ENCODING));
			
			for(byte[] field : command.getFieldsList()){
				helper.addArg(field);
			}
		}

		@Override
		public void encode(Hexists command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(3);
			helper.addArg(commandNames.get(CommandType.HEXISTS));
			helper.addArg(command.getKey().getBytes(ENCODING));
			helper.addArg(command.getField());
		}

		@Override
		public void encode(Hget command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(3);
			helper.addArg(commandNames.get(CommandType.HGET));
			helper.addArg(command.getKey().getBytes(ENCODING));
			helper.addArg(command.getField());
		}

		@Override
		public void encode(Hgetall command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(2);
			helper.addArg(commandNames.get(CommandType.HGETALL));
			helper.addArg(command.getKey().getBytes(ENCODING));
		}

		@Override
		public void encode(Hincrby command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(4);
			helper.addArg(commandNames.get(CommandType.HINCRBY));
			helper.addArg(command.getKey().getBytes(ENCODING));
			helper.addArg(command.getField());
			helper.addArg(command.getIncrement());
		}

		@Override
		public void encode(Hincrbyfloat command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(4);
			helper.addArg(commandNames.get(CommandType.HINCRBYFLOAT));
			helper.addArg(command.getKey().getBytes(ENCODING));
			helper.addArg(command.getField());
			helper.addArg(command.getIncrement());
		}

		@Override
		public void encode(Hkeys command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(2);
			helper.addArg(commandNames.get(CommandType.HKEYS));
			helper.addArg(command.getKey().getBytes(ENCODING));
		}

		@Override
		public void encode(Hlen command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(2);
			helper.addArg(commandNames.get(CommandType.HLEN));
			helper.addArg(command.getKey().getBytes(ENCODING));
		}

		@Override
		public void encode(Hmget command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(2+command.getFieldList().size());
			helper.addArg(commandNames.get(CommandType.HMGET));
			helper.addArg(command.getKey().getBytes(ENCODING));
			
			for(byte[] field : command.getFieldList()){
				helper.addArg(field);
			}
		}

		@Override
		public void encode(Hset command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(4);
			helper.addArg(commandNames.get(CommandType.HSET));
			helper.addArg(command.getKey().getBytes(ENCODING));
			helper.addArg(command.getField());
			helper.addArg(command.getValue());
		}

		@Override
		public void encode(Hsetnx command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(4);
			helper.addArg(commandNames.get(CommandType.HSETNX));
			helper.addArg(command.getKey().getBytes(ENCODING));
			helper.addArg(command.getField());
			helper.addArg(command.getValue());
		}

		@Override
		public void encode(Hvals command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(2);
			helper.addArg(commandNames.get(CommandType.HVALS));
			helper.addArg(command.getKey().getBytes(ENCODING));
		}

		@Override
		public void encode(Incr command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(2);
			helper.addArg(commandNames.get(CommandType.INCR));
			helper.addArg(command.getKey().getBytes(ENCODING));
		}

		@Override
		public void encode(Incrby command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(3);
			helper.addArg(commandNames.get(CommandType.INCRBY));
			helper.addArg(command.getKey().getBytes(ENCODING));
			helper.addArg(command.getIncrement());
		}

		@Override
		public void encode(Incrbyfloat command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(3);
			helper.addArg(commandNames.get(CommandType.INCRBYFLOAT));
			helper.addArg(command.getKey().getBytes(ENCODING));
			helper.addArg(command.getIncrement());
		}

		@Override
		public void encode(Info command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(2);
			helper.addArg(commandNames.get(CommandType.INFO));
			helper.addArg(command.getSection());
		}

		@Override
		public void encode(Keys command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(2);
			helper.addArg(commandNames.get(CommandType.KEYS));
			helper.addArg(command.getKey().getBytes(ENCODING));
		}

		@Override
		public void encode(Lpop command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(2);
			helper.addArg(commandNames.get(CommandType.LPOP));
			helper.addArg(command.getKey().getBytes(ENCODING));
		}
		
		@Override
		public void encode(Multi command, ByteBuf out) {
			encodeNoArgCommand(out, commandNames.get(CommandType.MULTI));
		}
		
		@Override
		public void encode(Ping command, ByteBuf out) {
			encodeNoArgCommand(out, commandNames.get(CommandType.PING));
		}

		@Override
		public void encode(Rename command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(3);
			helper.addArg(commandNames.get(CommandType.RENAME));
			helper.addArg(command.getKey().getBytes(ENCODING));
			helper.addArg(command.getNewKey().getBytes(ENCODING));
		}

		@Override
		public void encode(Renamex command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(3);
			helper.addArg(commandNames.get(CommandType.RENAMEX));
			helper.addArg(command.getKey().getBytes(ENCODING));
			helper.addArg(command.getNewKey().getBytes(ENCODING));
		}

		@Override
		public void encode(Role command, ByteBuf out) {
			encodeNoArgCommand(out, commandNames.get(CommandType.ROLE));
		}

		@Override
		public void encode(Rpop command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(2);
			helper.addArg(commandNames.get(CommandType.RPOP));
			helper.addArg(command.getKey().getBytes(ENCODING));
		}

		@Override
		public void encode(Rpoplpush command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(3);
			helper.addArg(commandNames.get(CommandType.RPOPLPUSH));
			helper.addArg(command.getSource());
			helper.addArg(command.getDestination());
		}

		@Override
		public void encode(Rpush command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(2+command.getValues().size());
			helper.addArg(commandNames.get(CommandType.RPUSH));
			helper.addArg(command.getKey().getBytes(ENCODING));
			
			for(String value :command.getValues()){
				helper.addArg(value.getBytes(ENCODING));
			}
		}

		@Override
		public void encode(Rpushx command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(3);
			helper.addArg(commandNames.get(CommandType.RPUSHX));
			helper.addArg(command.getKey().getBytes(ENCODING));
			helper.addArg(command.getValue().getBytes(ENCODING));
		}

		@Override
		public void encode(Sadd command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(1+command.getKeys().size());
			helper.addArg(commandNames.get(CommandType.SADD));
			
			for(String key : command.getKeys()){
				helper.addArg(key.getBytes(ENCODING));
			}
		}

		@Override
		public void encode(Scard command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(2);
			helper.addArg(commandNames.get(CommandType.SCARD));
			helper.addArg(command.getKey().getBytes(ENCODING));
		}

		@Override
		public void encode(Sdiff command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(1+command.getKeys().size());
			helper.addArg(commandNames.get(CommandType.SDIFF));
			
			for(String key : command.getKeys()){
				helper.addArg(key.getBytes(ENCODING));
			}
		}

		@Override
		public void encode(Sdiffstore command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(1+command.getKeys().size());
			helper.addArg(commandNames.get(CommandType.SDIFFSTORE));
			
			for(String key : command.getKeys()){
				helper.addArg(key.getBytes(ENCODING));
			}
		}

		@Override
		public void encode(Select command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(2);
			helper.addArg(commandNames.get(CommandType.SELECT));
			helper.addArg(command.getKey().getBytes(ENCODING));
		}

		@Override
		public void encode(Set command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(3);
			helper.addArg(commandNames.get(CommandType.SET));
			helper.addArg(command.getKey().getBytes(ENCODING));
			helper.addArg(command.getValue());
		}
		
		@Override
		public void encode(Setbit command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(4);
			helper.addArg(commandNames.get(CommandType.SETBIT));
			helper.addArg(command.getKey().getBytes(ENCODING));
			helper.addArg(command.getOffset());
			helper.addArg(command.getValue());
		}

		@Override
		public void encode(Setex command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(4);
			helper.addArg(commandNames.get(CommandType.SETEX));
			helper.addArg(command.getKey().getBytes(ENCODING));
			helper.addArg(String.valueOf(command.getExpiry()).getBytes(ENCODING));
			helper.addArg(command.getValue());
		}

		@Override
		public void encode(Setnx command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(3);
			helper.addArg(commandNames.get(CommandType.SETNX));
			helper.addArg(command.getKey().getBytes(ENCODING));
			helper.addArg(command.getValue());
		}

		@Override
		public void encode(Setrange command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(4);
			helper.addArg(commandNames.get(CommandType.SETRANGE));
			helper.addArg(command.getKey().getBytes(ENCODING));
			helper.addArg(command.getOffset());
			helper.addArg(command.getValue());
		}

		@Override
		public void encode(Time command, ByteBuf out) {
			encodeNoArgCommand(out, commandNames.get(CommandType.TIME));
		}
		
		@Override
		public void encode(Ttl command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(2);
			helper.addArg(commandNames.get(CommandType.TTL));
			helper.addArg(command.getKey().getBytes(ENCODING));
		}

		@Override
		public void encode(Type command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(2);
			helper.addArg(commandNames.get(CommandType.TYPE));
			helper.addArg(command.getKey().getBytes(ENCODING));
		}

		@Override
		public void encode(Watch command, ByteBuf out) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(command.getKeys().size()+1);
			helper.addArg(commandNames.get(CommandType.WATCH));
			
			for (String key:command.getKeys()) {
				helper.addArg(key.getBytes(ENCODING));
			}
		}
		
		private void encodeNoArgCommand(ByteBuf out, byte[] commandName) {
			EncodeHelper helper = new EncodeHelper(out);
			helper.addArgc(1);
			helper.addArg(commandName);
		}
		
		public void encode(com.mmmthatsgoodcode.redis.protocol.Command command, ByteBuf out) throws OperationNotSupportedException {
			if (command instanceof Append) encode((Append) command, out);
			else if (command instanceof Auth) encode((Auth) command, out);
			else if (command instanceof Bgrewriteaof) encode((Bgrewriteaof) command, out);
			else if (command instanceof Bgsave) encode((Bgsave) command, out);
			else if (command instanceof Bitcount) encode((Bitcount) command, out);
			else if (command instanceof Bitop) encode((Bitop) command, out);
			else if (command instanceof Bitpos) encode((Bitpos) command, out);
			else if (command instanceof Blpop) encode((Blpop) command, out);
			else if (command instanceof Brpop) encode((Brpop) command, out);
			else if (command instanceof Brpoplpush) encode((Brpoplpush) command, out);
			else if (command instanceof Clientgetname) encode((Clientgetname) command, out);
			else if (command instanceof Clientlist) encode((Clientlist) command, out);
			else if (command instanceof com.mmmthatsgoodcode.redis.protocol.command.Command) encode((com.mmmthatsgoodcode.redis.protocol.command.Command) command, out);
			else if (command instanceof Commandcount) encode((Commandcount) command, out);
			else if (command instanceof Commandgetkeys) encode((Commandgetkeys) command, out);
			else if (command instanceof Commandinfo) encode((Commandinfo) command, out);
			else if (command instanceof Configget) encode((Configget) command, out);
			else if (command instanceof Configresetstat) encode((Configresetstat) command, out);
			else if (command instanceof Configrewrite) encode((Configrewrite) command, out);
			else if (command instanceof Configset) encode((Configset) command, out);
			else if (command instanceof Dbsize) encode((Dbsize) command, out);
			else if (command instanceof Decr) encode((Decr) command, out);
			else if (command instanceof Decrby) encode((Decrby) command, out);
			else if (command instanceof Del) encode((Del) command, out);
			else if (command instanceof Discard) encode((Discard) command, out);
			else if (command instanceof Dump) encode((Dump) command, out);
			else if (command instanceof Echo) encode((Echo) command, out);
			else if (command instanceof Exec) encode((Exec) command, out);
			else if (command instanceof Exists) encode((Exists) command, out);
			else if (command instanceof Expire) encode((Expire) command, out);
			else if (command instanceof Expireat) encode((Expireat) command, out);
			else if (command instanceof Flushall) encode((Flushall) command, out);
			else if (command instanceof Flushdb) encode((Flushdb) command, out);
			else if (command instanceof Get) encode((Get) command, out);
			else if (command instanceof Getbit) encode((Getbit) command, out);
			else if (command instanceof Getrange) encode((Getbit) command, out);
			else if (command instanceof Getset) encode((Getset) command, out);
			else if (command instanceof Hdel) encode((Hdel) command, out);
			else if (command instanceof Hexists) encode((Hexists) command, out);
			else if (command instanceof Hget) encode((Hget) command, out);
			else if (command instanceof Hgetall) encode((Hgetall) command, out);
			else if (command instanceof Hincrby) encode((Hincrby) command, out);
			else if (command instanceof Hincrbyfloat) encode((Hincrbyfloat) command, out);
			else if (command instanceof Hkeys) encode((Hkeys) command, out);
			else if (command instanceof Hlen) encode((Hlen) command, out);
			else if (command instanceof Hmget) encode((Hmget) command, out);
			else if (command instanceof Hset) encode((Hset) command, out);
			else if (command instanceof Hsetnx) encode((Hsetnx) command, out);
			else if (command instanceof Hvals) encode((Hvals) command, out);
			else if (command instanceof Incr) encode((Incr) command, out);
			else if (command instanceof Incrby) encode((Incrby) command, out);
			else if (command instanceof Incrbyfloat) encode((Incrbyfloat) command, out);
			else if (command instanceof Info) encode((Info) command, out);
			else if (command instanceof Keys) encode((Keys) command, out);
			else if (command instanceof Lpop) encode((Lpop) command, out);
			else if (command instanceof Lpop) encode((Lpop) command, out);
			else if (command instanceof Multi) encode((Multi) command, out);
			else if (command instanceof Ping) encode((Ping) command, out);
			else if (command instanceof Rename) encode((Rename) command, out);
			else if (command instanceof Renamex) encode((Renamex) command, out);
			else if (command instanceof Role) encode((Role) command, out);
			else if (command instanceof Rpop) encode((Rpop) command, out);
			else if (command instanceof Rpoplpush) encode((Rpoplpush) command, out);
			else if (command instanceof Rpush) encode((Rpush) command, out);
			else if (command instanceof Rpushx) encode((Rpushx) command, out);
			else if (command instanceof Sadd) encode((Sadd) command, out);
			else if (command instanceof Scard) encode((Scard) command, out);
			else if (command instanceof Sdiff) encode((Sdiff) command, out);
			else if (command instanceof Sdiffstore) encode((Sdiffstore) command, out);
			else if (command instanceof Select) encode((Select) command, out);
			else if (command instanceof Set) encode((Set) command, out);
			else if (command instanceof Setbit) encode((Setbit) command, out);
			else if (command instanceof Setex) encode((Setex) command, out);
			else if (command instanceof Setnx) encode((Setnx) command,out);
			else if (command instanceof Setrange) encode((Setrange) command, out);
			else if (command instanceof Time) encode((Time) command,out);
			else if (command instanceof Ttl) encode((Ttl) command, out);
			else if (command instanceof Type) encode((Type) command, out);
			else if (command instanceof Watch) encode((Watch) command, out);
			//TODO fill Commands
			else throw new OperationNotSupportedException();
		}		

		@Override
		public void encode(Transaction transaction, ByteBuf out) throws OperationNotSupportedException {
			
			for (Command command:transaction) {
				encode(command, out);
			}
		}
	}
	
	public class Decoder implements Protocol.Decoder {

		private ReplyType currentReplyType = null; // stores the result of infer() for this decoder
		private int currentBytesExpected = 0; // how many bytes we are waiting to become available in this buffer
		private int paramLength = 0; // for multi bulk replies, the number of expected parameters
		private List<Reply> replies = new ArrayList<Reply>(); // for multi bulk replies, holds the completed reply objects
		private Decoder currentMultiBulkReplyDecoder = null; // for multi bulk replies, holds the decoder that is decoding the current reply
		
		public ReplyType replyType() {
			return currentReplyType;
		}
		
		@Override
		public Reply decode(ByteBuf in) throws UnrecognizedReplyException {
			
			if (currentReplyType == null || currentReplyType == ReplyType.UNKNOWN) currentReplyType = infer(in);
			
			switch(currentReplyType) {
			
				case STATUS:
					return decodeStatusReply(in);
				case ERROR:
					return decodeErrorReply(in);
				case INTEGER:
					return decodeIntegerReply(in);
				case BULK:
					return decodeBulkReply(in);
				case MULTI_BULK:
					return decodeMultiBulkReply(in);
				case UNKNOWN:
				default:
					throw new UnrecognizedReplyException();
			
			}
			
		}	
		
		/**
		 * Figure out what kind of Reply is in the buffer
		 * @param in
		 * @return
		 */
		protected ReplyType infer(ByteBuf in) {
			byte hint = in.readByte();
			
			if (hint == ReplyHintBytes.STATUS) return ReplyType.STATUS;
			if (hint == ReplyHintBytes.ERROR) return ReplyType.ERROR;
			if (hint == ReplyHintBytes.INTEGER) return ReplyType.INTEGER;
			if (hint == ReplyHintBytes.BULK) return ReplyType.BULK;
			if (hint == ReplyHintBytes.MULTI) return ReplyType.MULTI_BULK;
			
			LOG.warn("Redis reply \"{}\" not recognized", new String(new byte[]{hint}));
			in.readerIndex(in.readerIndex()-1);
			return ReplyType.UNKNOWN;
		}
		
		/**
		 * Decode contents of buffer in to an instance of StatusReply
		 * Expected format:
		 * +{status reply}CR+LF
		 */
		protected StatusReply decodeStatusReply(ByteBuf in) {
			// there is at least one delimiter in the buffer - we can do the decoding
			if (in.forEachByte(new HasDelimiter()) != -1) {
				byte[] statusCode = in.readBytes( in.forEachByte(new HasDelimiter()) - 1 - in.readerIndex() ).array(); // read up to the new line..
				StatusReply statusReply = new StatusReply(new String(statusCode));
				if (LOG.isDebugEnabled()) LOG.debug("Decoded status reply: \"{}\"", statusReply.value());
				in.readerIndex(in.readerIndex()+DELIMITER.length); // move reader index beyond the CRLF
				return statusReply;
			}
			
			return null;
		}
		
		/**
		 * Expected format:
		 * -{error type} {error message}DELIMITER
		 */
		protected ErrorReply decodeErrorReply(ByteBuf in) {
			
			if (in.forEachByte(new HasDelimiter()) != -1) {
				// there is a delimiter in this, we're good to parse
				byte[] errType = in.readBytes( in.forEachByte(ByteBufProcessor.FIND_LINEAR_WHITESPACE)-in.readerIndex() ).array(); // read up to the first white space
				in.readerIndex(in.readerIndex()+1); // move reader beyond the whitespace
				byte[] errMessage = in.readBytes( in.forEachByte(new HasDelimiter())-1-in.readerIndex() ).array(); // read up to the next white space
				in.readerIndex(in.readerIndex()+DELIMITER.length); // move reader index beyond the CRLF
				return new ErrorReply(new String(errType, ENCODING), new String(errMessage, ENCODING));
				
			}
			
			return null;
		}
		
		/**
		 * Expected format:
		 * :{integer-as-string}DELIMITER
		 */
		protected IntegerReply decodeIntegerReply(ByteBuf in) {
			if (in.forEachByte(new HasDelimiter()) != -1) {
				// there is a delimiter in this, we're good to parse
				byte[] intValue = in.readBytes( in.forEachByte(new HasDelimiter())-1-in.readerIndex() ).array(); 
				in.readerIndex(in.readerIndex()+DELIMITER.length); // move reader index beyond the CRLF
				return new IntegerReply(Integer.valueOf(new String(intValue, ENCODING)));
				
			}
			
			return null;
		}
		
		/**
		 * Expected format:
		 * ${attribute length}CR+LF{attribute}CR+LF
		 */
		protected BulkReply decodeBulkReply(ByteBuf in) {
			// wait for a delimiter to come
			if (in.forEachByte(new HasDelimiter()) != -1) {
				
				if (currentBytesExpected == 0) {
					LOG.debug("Starting from index {} ( {} readable )", in.readerIndex(), in.readableBytes());
		
//					LOG.debug("trying to decode {}", new String(UnpooledByteBufAllocator.DEFAULT.heapBuffer().writeBytes(in, in.readerIndex(), in.readableBytes()).array()));
					
					// so, there is at least one delimiter here, but do we have attribute length + 2 more bytes to read?
					byte[] attrLength = in.readBytes( in.forEachByte(new HasDelimiter()) - 1 - in.readerIndex() ).array();
					currentBytesExpected = Integer.valueOf( new String(attrLength) ); 
				}
				
				LOG.debug("Expecting {} bytes", currentBytesExpected);
				
				if (currentBytesExpected == -1) {
					// no result
					currentBytesExpected = 0;

					in.readerIndex(in.readerIndex()+DELIMITER.length); // move reader index beyond the CRLF
					return new BulkReply(null);// done with this reply
				} else if (in.readableBytes() >= currentBytesExpected+(DELIMITER.length*2)) { // there should be 2x delimiters in here, plus the content
					LOG.debug("There are sufficient bytes in this buffer to finish decoding");
					// we have the remainder of the reply in this buffer. Finish reading.
					in.readerIndex(in.readerIndex()+DELIMITER.length); // move reader index beyond the CRLF
					byte[] attribute = in.readBytes(currentBytesExpected).array();
					currentBytesExpected = 0;

					in.readerIndex(in.readerIndex()+DELIMITER.length); // move reader index beyond the CRLF
					return new BulkReply(attribute); // done with this reply
				}
				
				// expected reply length isnt -1 and buffer contains fewer bytes. Wait for another invocation of decodeBulkReply() 
				LOG.debug("Waiting for more data in the buffer");
				
			}
			
			return null;
		}
		
		/**
		 * Expected format:
		 * *{number of bulk replies expected}CR+LF
		 * ${attribute length}CR+LF{attribute}CR+LF
		 * .
		 * .
		 * ${attribute length}CR+LF{attribute}CR+LF
		 * @throws UnrecognizedReplyException 
		 */
		protected MultiBulkReply decodeMultiBulkReply(ByteBuf in) throws UnrecognizedReplyException {
			// wait for a delimiter to come
			if (in.forEachByte(new HasDelimiter()) != -1) {
				
				// read parameter count if we haven't already
				if (paramLength == 0) {
					byte[] paramLengthBytes = in.readBytes( in.forEachByte(new HasDelimiter()) - 1 - in.readerIndex() ).array();
					paramLength = Integer.valueOf( new String(paramLengthBytes, ENCODING) );
					if (paramLength == -1) {
						LOG.debug("Null MultiBulk reply!");
						in.readerIndex(in.readerIndex()+DELIMITER.length); // move reader index beyond the CRLF
						return new MultiBulkReply(new ArrayList<Reply>());
					}
					LOG.debug("Expecting {} Replies", paramLength);
					in.readerIndex(in.readerIndex()+DELIMITER.length); // move reader index beyond the CRLF

					LOG.debug("Creating new Decoder for reply in MultiBulkReply");

					currentMultiBulkReplyDecoder = new Decoder();
				}
								
				while (replies.size() < paramLength && in.readableBytes() > 0) {
					LOG.debug("Decoding Reply {} of {} in MultiBulkReply", replies.size()+1, paramLength);

//					try {
						
						Reply currentMultiBulkReply = currentMultiBulkReplyDecoder.decode(in);
						if (currentMultiBulkReply != null) {
							replies.add(currentMultiBulkReply);
	
							// see if we need to prepare the next reply
							if (replies.size() != paramLength) {
								currentMultiBulkReplyDecoder = new Decoder();
								return null; // not enough bytes in buffer to infer the next reply.. wait for more.
	
							}
								
							// done!
							return new MultiBulkReply(replies);
							
							
						} else {
							// there wasnt enough data in here for the bulk reply to decode. 
							LOG.debug("Waiting for more data");
							return null;
						}
						
//					} catch (UnrecognizedReplyException e) {
//						// thats fine
//						LOG.debug("Infer threw UnrecognizedReplyException, waiting for more data");
//						return null;
//					}
									
				}
				
			}
			
			
			return null;
		}
		
	}

	// a ByteBufProcessor that finds delimiters
	public static class HasDelimiter implements ByteBufProcessor {

		private byte previous;
		
		@Override
		public boolean process(byte value) throws Exception {
			if (value == '\n' && previous == '\r') return false;
			previous = value;
			return true;
		}
		
	}
	
	// Character encoding
	public static final Charset ENCODING = Charset.forName("UTF-8");
	
	// Command delimiter
	public static final byte[] DELIMITER = "\r\n".getBytes(ENCODING);
	
	// Allocator to grab buffers from
	protected static ByteBufAllocator byteBufAllocator = new PooledByteBufAllocator();
	
	public static final byte ARGC_BEGIN = "*".getBytes(ENCODING)[0];
	public static final byte ARG_LENGTH_BEGIN = "$".getBytes(ENCODING)[0];
	public static final byte ARG_INT_BEGIN = ":".getBytes(ENCODING)[0];
	public static final byte ARG_ERR_BEGIN = "-".getBytes(ENCODING)[0];
	


	private static final Map<CommandType, byte[]> commandNames = new ImmutableMap.Builder<CommandType, byte[]>()
			.put(CommandType.APPEND, "APPEND".getBytes(ENCODING))
			.put(CommandType.AUTH, "AUTH".getBytes(ENCODING))
			.put(CommandType.BGREWRITEAOF, "BGREWRITEAOF".getBytes(ENCODING))
			.put(CommandType.BGSAVE, "BGSAVE".getBytes(ENCODING))
			.put(CommandType.BITCOUNT, "BITCOUNT".getBytes(ENCODING))
			.put(CommandType.BITOP, "BITOP".getBytes(ENCODING))
			.put(CommandType.BITPOS, "BITPOS".getBytes(ENCODING))
			.put(CommandType.BLPOP, "BLPOP".getBytes(ENCODING))
			.put(CommandType.BRPOP, "BRPOP".getBytes(ENCODING))
			.put(CommandType.BRPOPLPUSH, "BRPOPLPUSH".getBytes(ENCODING))
			.put(CommandType.CLIENTGETNAME, "CLIENT GETNAME".getBytes(ENCODING))
			.put(CommandType.CLIENTLIST, "CLIENT LIST".getBytes(ENCODING))
			.put(CommandType.COMMAND, "COMMAND".getBytes(ENCODING))
			.put(CommandType.COMMANDCOUNT, "COMMAND COUNT".getBytes(ENCODING))
			.put(CommandType.COMMANDGETKEYS, "COMMAND GETKEYS".getBytes(ENCODING))
			.put(CommandType.COMMANDINFO, "COMMAND INFO".getBytes(ENCODING))
			.put(CommandType.CONFIGGET, "CONFIG GET".getBytes(ENCODING))
			.put(CommandType.CONFIGRESETSTAT, "CONFIG RESETSTAT".getBytes(ENCODING))
			.put(CommandType.CONFIGREWRITE, "CONFIG REWRITE".getBytes(ENCODING))
			.put(CommandType.CONFIGSET, "CONFIG SET".getBytes(ENCODING))
			.put(CommandType.DBSIZE, "DBSIZE".getBytes(ENCODING))
			.put(CommandType.DECR, "DECR".getBytes(ENCODING))
			.put(CommandType.DECRBY, "DECRBY".getBytes(ENCODING))
			.put(CommandType.DEL, "DEL".getBytes(ENCODING))
			.put(CommandType.DISCARD, "DISCARD".getBytes(ENCODING))
			.put(CommandType.DUMP, "DUMP".getBytes(ENCODING))
			.put(CommandType.ECHO, "ECHO".getBytes(ENCODING))
			.put(CommandType.EXEC, "EXEC".getBytes(ENCODING))
			.put(CommandType.EXISTS, "EXISTS".getBytes(ENCODING))
			.put(CommandType.EXPIRE, "EXPIRE".getBytes(ENCODING))
			.put(CommandType.EXPIREAT, "EXPIREAT".getBytes(ENCODING))
			.put(CommandType.FLUSHALL, "FLUSHALL".getBytes(ENCODING))
			.put(CommandType.FLUSHDB, "FLUSHDB".getBytes(ENCODING))
			.put(CommandType.GET, "GET".getBytes(ENCODING))
			.put(CommandType.GETBIT, "GETBIT".getBytes(ENCODING))
			.put(CommandType.GETRANGE, "GETRANGE".getBytes(ENCODING))
			.put(CommandType.GETSET, "GETSET".getBytes(ENCODING))
			.put(CommandType.HDEL, "HDEL".getBytes(ENCODING))
			.put(CommandType.HEXISTS, "HEXISTS".getBytes(ENCODING))
			.put(CommandType.HGET, "HGET".getBytes(ENCODING))
			.put(CommandType.HGETALL, "HGETALL".getBytes(ENCODING))
			.put(CommandType.HINCRBY, "HINCRBY".getBytes(ENCODING))
			.put(CommandType.HINCRBYFLOAT, "HINCRBYFLOAT".getBytes(ENCODING))
			.put(CommandType.HKEYS, "HKEYS".getBytes(ENCODING))
			.put(CommandType.HLEN, "HLEN".getBytes(ENCODING))
			.put(CommandType.HMGET, "HMGET".getBytes(ENCODING))
			.put(CommandType.HSET, "HSET".getBytes(ENCODING))
			.put(CommandType.HSETNX, "HSETNX".getBytes(ENCODING))
			.put(CommandType.HVALS, "HVALS".getBytes(ENCODING))
			.put(CommandType.INCR, "INCR".getBytes(ENCODING))
			.put(CommandType.INCRBY, "INCRBY".getBytes(ENCODING))
			.put(CommandType.INCRBYFLOAT, "INCRBYFLOAT".getBytes(ENCODING))
			.put(CommandType.INFO, "INFO".getBytes(ENCODING))
			.put(CommandType.KEYS, "KEYS".getBytes(ENCODING))
			.put(CommandType.LPOP, "LPOP".getBytes(ENCODING))
			.put(CommandType.MULTI, "MULTI".getBytes(ENCODING))
			.put(CommandType.PING, "PING".getBytes(ENCODING))
			.put(CommandType.RENAME, "RENAME".getBytes(ENCODING))
			.put(CommandType.RENAMEX, "RENAMEX".getBytes(ENCODING))
			.put(CommandType.ROLE, "ROLE".getBytes(ENCODING))
			.put(CommandType.RPOP, "RPOP".getBytes(ENCODING))
			.put(CommandType.RPOPLPUSH, "RPOPLPUSH".getBytes(ENCODING))
			.put(CommandType.RPUSH, "RPUSH".getBytes(ENCODING))
			.put(CommandType.RPUSHX, "RPUSHX".getBytes(ENCODING))
			.put(CommandType.SADD, "SADD".getBytes(ENCODING))
			.put(CommandType.SCARD, "SCARD".getBytes(ENCODING))
			.put(CommandType.SDIFF, "SDIFF".getBytes(ENCODING))
			.put(CommandType.SDIFFSTORE, "SDIFFSTORE".getBytes(ENCODING))
			.put(CommandType.SELECT, "SELECT".getBytes(ENCODING))
			.put(CommandType.SET, "SET".getBytes(ENCODING))
			.put(CommandType.SETBIT, "SETBIT".getBytes(ENCODING))
			.put(CommandType.SETEX, "SETEX".getBytes(ENCODING))
			.put(CommandType.SETNX, "SETNX".getBytes(ENCODING))
			.put(CommandType.SETRANGE, "SETRANGE".getBytes(ENCODING))
			.put(CommandType.TIME, "TIME".getBytes(ENCODING))
			.put(CommandType.TTL, "TTL".getBytes(ENCODING))
			.put(CommandType.TYPE, "TYPE".getBytes(ENCODING))
			.put(CommandType.WATCH, "WATCH".getBytes(ENCODING))
			.build();
	//TODO fill Commands

	
	private Encoder encoder = new Encoder();
	
	public Redis2TextProtocol() {
		
	}
	
	@Override
	public ByteBufAllocator getByteBufAllocator() {
		return byteBufAllocator;
	}

	@Override
	public Encoder getEncoder() {
		return encoder;
	}

	@Override
	public Decoder getDecoder() {
		
		return new Decoder();
		
	}

	
}
