package com.mmmthatsgoodcode.redis.protocol;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;

import com.mmmthatsgoodcode.redis.Protocol;
import com.mmmthatsgoodcode.redis.protocol.response.*;

public abstract class Response<T> extends Protocol {
	
	/**
	 * A simple wrapper for the decoded value of this response to store a default "not set" value
	 * @author aszerdahelyi
	 *
	 * @param <T> Type of the actual value
	 */
	public static class ResponseValue<T> {
		
		private T value;
		private boolean set = false;
		
		public ResponseValue(T value) {
			this.value = value;
			this.set = true;
		}
		
		private ResponseValue() {
			
		}
		
		public T value() {
			return value;
		}
		
		public boolean isSet() {
			return set;
		}
		
		public static final ResponseValue none() {
			return new ResponseValue();
		}
		
	}
	
	/**
	 * The first byte of the Redis response indicating the response type.
	 * @author aszerdahelyi
	 *
	 */
	public static class ResponseHintBytes {
		
		public static final byte STATUS = "+".getBytes()[0];
		public static final byte ERROR = "-".getBytes()[0];
		public static final byte INTEGER = ":".getBytes()[0];
		public static final byte BULK = "$".getBytes()[0];
		public static final byte MULTI = "*".getBytes()[0];
		
	}
	
	protected final ByteBuf in;
	protected ResponseValue<T> value = ResponseValue.none();
	
	public Response(ByteBuf in) {
		this.in = in;
		this.in.readerIndex(1);

	}
	
	public T value() throws InterruptedException {
		if (value.isSet() == false) throw new IllegalStateException();
		return this.value.value();
	}
	
	protected void setValue(T value) {
		this.value = new ResponseValue(value);
	}
	
	/**
	 * Attempt to decode the Response from the current contents of the inbound buffer.
	 * @return
	 */
	public abstract boolean decode();

	
	/**
	 * Infer the type of the Redis response by the first byte in this ByteBuf
	 * @param in
	 * @return Inferred Response type
	 */
	public static final Response infer(ByteBuf in) {
		
		if (in.getByte(0) == ResponseHintBytes.STATUS) return new StatusResponse(in);
		if (in.getByte(0) == ResponseHintBytes.ERROR) return new ErrorResponse(in);
		if (in.getByte(0) == ResponseHintBytes.INTEGER) return new IntegerResponse(in);
		if (in.getByte(0) == ResponseHintBytes.BULK) return new BulkResponse(in);
		if (in.getByte(0) == ResponseHintBytes.MULTI) return new MultiBulkResponse(in);
		
		throw new IllegalArgumentException("Redis response "+new String(new byte[]{in.getByte(0)})+" not recognized");
		
		
	}
	
}
