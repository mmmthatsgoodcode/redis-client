package com.mmmthatsgoodcode.redis;

import java.util.Arrays;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;
import io.netty.buffer.PooledByteBufAllocator;

public class Util {

	public static class ByteArrayInBuf implements ByteBufProcessor {

		final byte[] buffer;
		final byte[] needle;
		private int bytesRead=0;
		
		public ByteArrayInBuf(byte[] needle) {
			this.needle = needle;
			this.buffer = new byte[needle.length];
		}
		
		@Override
		public boolean process(byte value) throws Exception {
			bytesRead++;
			addToBuffer(value);
			if (bytesRead >= needle.length) {
				return (Arrays.equals(needle, buffer) == false);
			}
			
			return true;
		}
		
		private void addToBuffer(byte value) {
			
			for(int b=0;b<(buffer.length-1);b++) {
				buffer[b] = buffer[b+1];
			}

			buffer[buffer.length-1] = value;
		}
		
	}
	
}
