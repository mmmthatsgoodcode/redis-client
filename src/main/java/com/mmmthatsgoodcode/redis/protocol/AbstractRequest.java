package com.mmmthatsgoodcode.redis.protocol;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;

import com.mmmthatsgoodcode.redis.Protocol;

public abstract class AbstractRequest<T extends Response> extends Protocol implements Request<T> {

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
	protected final PendingResponse<T> response = new PendingResponse<T>(this);
	public static final byte ARGC_BEGIN = "*".getBytes(ENCODING)[0];
	public static final byte ARG_LENGTH_BEGIN = "$".getBytes(ENCODING)[0];
	
	
	/* (non-Javadoc)
	 * @see com.mmmthatsgoodcode.redis.protocol.RequestInterface#encode()
	 */
	@Override
	public abstract ByteBuf encode(); 
	/* (non-Javadoc)
	 * @see com.mmmthatsgoodcode.redis.protocol.RequestInterface#getName()
	 */
	@Override
	public abstract byte[] getName();
	
	/* (non-Javadoc)
	 * @see com.mmmthatsgoodcode.redis.protocol.RequestInterface#responseReceived(T)
	 */
	@Override
	public void responseReceived(T response) {
		response.setRequest(this);
	}
	
	/* (non-Javadoc)
	 * @see com.mmmthatsgoodcode.redis.protocol.RequestInterface#getArgc()
	 */
	@Override
	public int getArgc() {
		return argc;
	}
	
	/* (non-Javadoc)
	 * @see com.mmmthatsgoodcode.redis.protocol.RequestInterface#setArgc(int)
	 */
	@Override
	public void setArgc(int argc) {
		this.argc = argc;
	}
	
	/* (non-Javadoc)
	 * @see com.mmmthatsgoodcode.redis.protocol.RequestInterface#getResponse()
	 */
	@Override
	public PendingResponse<T> getResponse() {
		return this.response;
	}
	
	
}
