package com.mmmthatsgoodcode.redis.protocol;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;

import com.mmmthatsgoodcode.redis.Protocol;
import com.mmmthatsgoodcode.redis.protocol.response.*;

public abstract class Response<T> extends Protocol {
	
	private final static Logger LOG = LoggerFactory.getLogger(Response.class);
	
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
		
		public String toString() {
			if (set == false) return "pending";
			return value.toString();
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
	protected Request request = null;
	
	public Response(ByteBuf in) {
		this.in = in;

	}
	
	public T value() throws IllegalStateException {
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
		
		byte hint = in.readByte();
		LOG.debug("Looking at hint {}", new String(new byte[]{hint}));
		
		if (hint == ResponseHintBytes.STATUS) return new StatusResponse(in);
		if (hint == ResponseHintBytes.ERROR) return new ErrorResponse(in);
		if (hint == ResponseHintBytes.INTEGER) return new IntegerResponse(in);
		if (hint == ResponseHintBytes.BULK) return new BulkResponse(in);
		if (hint == ResponseHintBytes.MULTI) return new MultiBulkResponse(in);
		
		LOG.debug("Redis response \"{}\" not recognized", new String(new byte[]{hint}));
		return null;
		
	}
	
	public void setRequest(Request request) {
		this.request = request;
	}
	
	public Request getRequest() {
		return request;
	}
	
	public String toString() {
		return this.getClass().getSimpleName()+":"+value;
		
	}
	
}
