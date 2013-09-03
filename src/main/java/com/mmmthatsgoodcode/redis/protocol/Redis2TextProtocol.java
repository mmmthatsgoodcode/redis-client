package com.mmmthatsgoodcode.redis.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufProcessor;
import io.netty.buffer.PooledByteBufAllocator;

import java.nio.charset.Charset;
import java.util.Map;

import javax.naming.OperationNotSupportedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.mmmthatsgoodcode.redis.Protocol;
import com.mmmthatsgoodcode.redis.client.Transaction;
import com.mmmthatsgoodcode.redis.client.UnrecognizedReplyException;
import com.mmmthatsgoodcode.redis.protocol.Command;
import com.mmmthatsgoodcode.redis.protocol.command.Exec;
import com.mmmthatsgoodcode.redis.protocol.command.Exists;
import com.mmmthatsgoodcode.redis.protocol.command.Get;
import com.mmmthatsgoodcode.redis.protocol.command.Multi;
import com.mmmthatsgoodcode.redis.protocol.command.Ping;
import com.mmmthatsgoodcode.redis.protocol.command.Set;
import com.mmmthatsgoodcode.redis.protocol.command.Setex;
import com.mmmthatsgoodcode.redis.protocol.command.Setnx;
import com.mmmthatsgoodcode.redis.protocol.command.Watch;
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
		public ByteBuf encode(Exec command) {
			return encodeNoArgCommand(command, commandNames.get(CommandType.EXEC));
		}

		@Override
		public ByteBuf encode(Exists command) {
			EncodeHelper out = new EncodeHelper(Redis2TextProtocol.this.getByteBufAllocator().buffer());
			out.addArg(commandNames.get(CommandType.EXISTS));
			out.addArg(command.getKey().getBytes(ENCODING));
			return out.buffer();
		}

		@Override
		public ByteBuf encode(Get command) {
			EncodeHelper out = new EncodeHelper(Redis2TextProtocol.this.getByteBufAllocator().buffer());
			out.addArg(commandNames.get(CommandType.GET));
			out.addArg(command.getKey().getBytes(ENCODING));
			return out.buffer();
		}

		@Override
		public ByteBuf encode(Multi command) {
			return encodeNoArgCommand(command, commandNames.get(CommandType.MULTI));
		}
		
		@Override
		public ByteBuf encode(Ping command) {
			return encodeNoArgCommand(command, commandNames.get(CommandType.PING));
		}

		@Override
		public ByteBuf encode(Set command) {
			EncodeHelper out = new EncodeHelper(Redis2TextProtocol.this.getByteBufAllocator().buffer());
			out.addArg(commandNames.get(CommandType.SET));
			out.addArg(command.getKey().getBytes(ENCODING));
			out.addArg(command.getValue());

			return out.buffer();
		}

		@Override
		public ByteBuf encode(Setnx command) {
			return encode((Set) command);
		}

		@Override
		public ByteBuf encode(Watch command) {
			return encodeNoArgCommand(command, commandNames.get(CommandType.PING));
		}	
		
		private ByteBuf encodeNoArgCommand(Command command, byte[] commandName) {
			EncodeHelper out = new EncodeHelper(Redis2TextProtocol.this.getByteBufAllocator().buffer());
			out.addArg(commandName);
			return out.buffer();
		}

		@Override
		public ByteBuf encode(Setex command) {
			EncodeHelper out = new EncodeHelper(Redis2TextProtocol.this.getByteBufAllocator().buffer());
			out.addArg(command.getName());
			out.addArg(command.getKey().getBytes(ENCODING));
			out.addArg(String.valueOf(command.getExpiry()).getBytes(ENCODING));
			out.addArg(command.getValue());
			
			return out.buffer();
		}
		
		public ByteBuf encode(Command command) throws OperationNotSupportedException {
			throw new OperationNotSupportedException();
		}

		@Override
		public ByteBuf encodeTransaction(Transaction transaction) throws OperationNotSupportedException {
			ByteBuf out = byteBufAllocator.buffer();
			
			for (Command command:transaction) {
				ByteBuf rbuff = encode(command);
				out.writeBytes(rbuff);
				rbuff.release();
			}
			
			return out;
		}
		
	}
	
	public class Decoder implements Protocol.Decoder {

		@Override
		public Reply decode(ByteBuf in) throws UnrecognizedReplyException {
			
			switch(infer(in)) {
			
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
			
			if (LOG.isDebugEnabled()) LOG.debug("Redis reply \"{}\" not recognized", new String(new byte[]{hint}));
			return ReplyType.UNKNOWN;
		}
		
		/**
		 * Decode contents of buffer in to an instance of StatusReply
		 * Expected format:
		 * +{status reply}CR+LF
		 */
		protected StatusReply decodeStatusReply(ByteBuf in) {
			// there is at least one delimiter in the buffer - we can do the decoding
			if (in.forEachByte(HAS_DELIMITER) != -1) {
				byte[] statusCode = in.readBytes( in.forEachByte(HAS_DELIMITER) - in.readerIndex() ).array(); // read up to the new line..
				StatusReply statusReply = new StatusReply(new String(statusCode));
				if (LOG.isDebugEnabled()) LOG.debug("Decoded status reply: \"{}\"", statusReply.value());
				return statusReply;
			}
			
			return null;
		}
		
		/**
		 * Expected format:
		 * -{error type} {error message}DELIMITER
		 */
		protected ErrorReply decodeErrorReply(ByteBuf in) {
			
			if (in.forEachByte(HAS_DELIMITER) != -1) {
				// there is a delimiter in this, we're good to parse
				byte[] errType = in.readBytes( in.forEachByte(ByteBufProcessor.FIND_LINEAR_WHITESPACE)-in.readerIndex() ).array(); // read up to the first white space
				in.readerIndex(in.readerIndex()+1); // move reader beyond the whitespace
				byte[] errMessage = in.readBytes( in.forEachByte(HAS_DELIMITER)-in.readerIndex() ).array(); // read up to the next white space
				return new ErrorReply(new String(errType, ENCODING), new String(errMessage, ENCODING));
				
			}
			
			return null;
		}
		
		/**
		 * Expected format:
		 * :{integer-as-string}DELIMITER
		 */
		protected IntegerReply decodeIntegerReply(ByteBuf in) {
			if (in.forEachByte(HAS_DELIMITER) != -1) {
				// there is a delimiter in this, we're good to parse
				byte[] intValue = in.readBytes( in.forEachByte(HAS_DELIMITER)-in.readerIndex() ).array(); 
				return new IntegerReply(Integer.valueOf(new String(intValue, ENCODING)));
				
			}
			
			return null;
		}
		
		/**
		 * Expected format:
		 * ${attribute length}CR+LF{attribute}CR+LF
		 */
		protected BulkReply decodeBulkReply(ByteBuf in) {
			return null;
		}
		
		protected MultiBulkReply decodeMultiBulkReply(ByteBuf in) {
			return null;
		}
		
	}

	// a ByteBufProcessor that finds delimiters
	public static final ByteBufProcessor HAS_DELIMITER = ByteBufProcessor.FIND_CRLF;
	
	// Character encoding
	public static final Charset ENCODING = Charset.forName("UTF-8");
	
	// Command delimiter
	public static final byte[] DELIMITER = "\r\n".getBytes(ENCODING);
	
	// Allocator to grab buffers from
	protected static ByteBufAllocator byteBufAllocator = new PooledByteBufAllocator();
	
	public static final byte ARGC_BEGIN = "*".getBytes(ENCODING)[0];
	public static final byte ARG_LENGTH_BEGIN = "$".getBytes(ENCODING)[0];
	


	private static final Map<CommandType, byte[]> commandNames = new ImmutableMap.Builder<CommandType, byte[]>()
			.put(CommandType.GET, "GET".getBytes(ENCODING))
			.put(CommandType.EXEC, "EXEC".getBytes(ENCODING))
			.put(CommandType.EXISTS, "EXISTS".getBytes(ENCODING))
			.put(CommandType.MULTI, "MULTI".getBytes(ENCODING))
			.put(CommandType.PING, "PING".getBytes(ENCODING))
			.put(CommandType.SET, "SET".getBytes(ENCODING))
			.put(CommandType.SETNX, "SETNX".getBytes(ENCODING))
			.put(CommandType.WATCH, "WATCH".getBytes(ENCODING))
			.build();

	
	private Decoder decoder = new Decoder();
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
		return decoder;
	}

	
}
