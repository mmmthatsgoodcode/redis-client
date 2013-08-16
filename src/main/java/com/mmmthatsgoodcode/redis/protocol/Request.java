package com.mmmthatsgoodcode.redis.protocol;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;

import com.mmmthatsgoodcode.redis.Protocol;

public abstract class Request extends Protocol {

	public static class EncodeHelper {
		
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
	
	
	protected int argc = 2;
	protected PendingResponse response = new PendingResponse();
	public static final byte ARGC_BEGIN = "*".getBytes(ENCODING)[0];
	public static final byte ARG_LENGTH_BEGIN = "$".getBytes(ENCODING)[0];
	
	
	public abstract ByteBuf encode(); 
	public abstract byte[] getName();
	
	public int getArgc() {
		return argc;
	}
	
	public void setArgc(int argc) {
		this.argc = argc;
	}
	
	public PendingResponse getResponse() {
		return this.response;
	}
	
	
	
	
}
