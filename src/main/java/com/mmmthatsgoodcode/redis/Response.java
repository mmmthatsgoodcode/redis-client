package com.mmmthatsgoodcode.redis;

import java.nio.charset.Charset;

public class Response<T> {

	public final static Charset ENCODING = Charset.forName("UTF-8");
	private T value = null;
	
	public Response(T value) {
		setValue(value);
	}
	
	public void setValue(T value) {
		this.value = value;
	}
	public T getValue() {
		return value;
	}
	
	public boolean hasValue() {
		return (value != null);
	}
		
	public static class Delimiters {
		
		public static final byte[] NEXT_PART = "+".getBytes(ENCODING);
		public static final byte[] NEXT_RESULT = "\n".getBytes(ENCODING);
		public static final byte[] RESPONSE_END = "\r".getBytes(ENCODING);
		
	}
	
	public String toString() {
		return hasValue()==true?getValue().toString():null;
	}
	
	
}
