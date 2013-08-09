package com.mmmthatsgoodcode.redis;

import java.nio.charset.Charset;

public class Response {

	public final static Charset ENCODING = Charset.forName("UTF-8");
	
	
	public static class Delimiters {
		
		public static final byte[] NEXT_PART = "+".getBytes(ENCODING);
		public static final byte[] NEXT_RESULT = "\n".getBytes(ENCODING);
		public static final byte[] RESPONSE_END = "\r".getBytes(ENCODING);
		
	}
	
	
}
