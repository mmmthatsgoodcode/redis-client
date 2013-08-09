package com.mmmthatsgoodcode.redis;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public abstract class Command {

	public final static Charset ENCODING = Charset.forName("UTF-8");
	protected ResponseContainer response = new ResponseContainer(this);
	
	public static class Delimiters {
		
		public static final byte[] PARAMS_BEGIN = " ".getBytes(ENCODING);
		public static final byte[] NEXT_PARAM = " ".getBytes(ENCODING);
		public static final byte[] COMMAND_END = "\r\n".getBytes(ENCODING);
		
	}
	
	private List<String> parameters = new ArrayList<String>();
		
	public abstract String getName();
	
	public void addParamter(String parameter) {
		parameters.add(parameter);
	}
	
	public List<String> getParameters() {
		return parameters;
	}
	
	public ByteBuf encode() {
		
		ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer();
		buf.writeBytes(getName().getBytes(ENCODING));
		if (getParameters().size() > 0) {
			buf.writeBytes(Delimiters.PARAMS_BEGIN);
			for (String parameter:getParameters()) {
				buf.writeBytes(parameter.getBytes(ENCODING));
			}
		}
		
		buf.writeBytes(Delimiters.COMMAND_END);
		return buf;
		
	}
	
	public ResponseContainer getResponse() {
		return response;
	}
	
}
