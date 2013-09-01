package com.mmmthatsgoodcode.redis.protocol;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;

import com.mmmthatsgoodcode.redis.Protocol;
import com.mmmthatsgoodcode.redis.protocol.reply.*;

public abstract class AbstractReply<T> extends Protocol implements Reply<T> {
	
	private final static Logger LOG = LoggerFactory.getLogger(AbstractReply.class);
	
	/**
	 * A simple wrapper for the decoded value of this Reply to store a default "not set" value
	 * @author aszerdahelyi
	 *
	 * @param <T> Type of the actual value
	 */
	public static class ReplyValue<T> {
		
		private T value;
		private boolean set = false;
		
		public ReplyValue(T value) {
			this.value = value;
			this.set = true;
		}
		
		private ReplyValue() {
			
		}
		
		public T value() {
			return value;
		}
		
		public boolean isSet() {
			return set;
		}
		
		public static final ReplyValue none() {
			return new ReplyValue();
		}
		
		public String toString() {
			if (set == false) return "pending";
			return value==null?null:value.toString();
		}
		
	}
	
	/**
	 * The first byte of the Redis reply indicating the reply type.
	 * @author aszerdahelyi
	 *
	 */
	public static class ReplyHintBytes {
		
		public static final byte STATUS = "+".getBytes()[0];
		public static final byte ERROR = "-".getBytes()[0];
		public static final byte INTEGER = ":".getBytes()[0];
		public static final byte BULK = "$".getBytes()[0];
		public static final byte MULTI = "*".getBytes()[0];
		
	}
	
	protected final ByteBuf in;
	protected ReplyValue<T> value = ReplyValue.none();
	protected Command command = null;
	
	public AbstractReply(ByteBuf in) {
		this.in = in;

	}
	
	/* (non-Javadoc)
	 * @see com.mmmthatsgoodcode.redis.protocol.Reply#value()
	 */
	@Override
	public T value() throws IllegalStateException {
		if (value.isSet() == false) throw new IllegalStateException();
		return this.value.value();
	}
	
	protected void setValue(T value) {
		this.value = new ReplyValue(value);
	}
	
	/* (non-Javadoc)
	 * @see com.mmmthatsgoodcode.redis.protocol.Reply#decode()
	 */
	@Override
	public abstract boolean decode();

	
	/**
	 * Infer the type of the Redis Reply by the first byte in this ByteBuf
	 * @param in
	 * @return Inferred Reply type
	 */
	public static final Reply infer(ByteBuf in) {
		
		byte hint = in.readByte();
		LOG.debug("Looking at hint {}", new String(new byte[]{hint}));
		
		if (hint == ReplyHintBytes.STATUS) return new StatusReply(in);
		if (hint == ReplyHintBytes.ERROR) return new ErrorReply(in);
		if (hint == ReplyHintBytes.INTEGER) return new IntegerReply(in);
		if (hint == ReplyHintBytes.BULK) return new BulkReply(in);
		if (hint == ReplyHintBytes.MULTI) return new MultiBulkReply(in);
		
		LOG.debug("Redis reply \"{}\" not recognized", new String(new byte[]{hint}));
		return null;
		
	}
	
	/* (non-Javadoc)
	 * @see com.mmmthatsgoodcode.redis.protocol.Reply#setCommand(com.mmmthatsgoodcode.redis.protocol.Command)
	 */
	@Override
	public void setCommand(Command command) {
		this.command = command;
	}
	
	/* (non-Javadoc)
	 * @see com.mmmthatsgoodcode.redis.protocol.Reply#getCommand()
	 */
	@Override
	public Command getCommand() {
		return command;
	}
	
	public String toString() {
		return this.getClass().getSimpleName()+":"+value;
		
	}
	
}
