package com.mmmthatsgoodcode.redis.protocol.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;
import io.netty.buffer.UnpooledByteBufAllocator;

import com.mmmthatsgoodcode.redis.protocol.Response;

/**
 * This should contain a single, arbitary-length incoming parameter as specified by ${length}DELIMITER
 * @author aszerdahelyi
 *
 */
public class BulkResponse extends Response<String> {

	private static final Logger LOG = LoggerFactory.getLogger(BulkResponse.class);
	private int currentBytesExpected = 0; // how many bytes we are waiting to become available in this buffer
	
	public BulkResponse(ByteBuf in) {
		super(in);
	}

	/**
	 * Expected format:
	 * ${attribute length}CR+LF{attribute}CR+LF
	 */
	@Override
	public boolean decode() {
		if (this.in.forEachByte(ByteBufProcessor.FIND_CRLF) != -1) {
			
			if (currentBytesExpected == 0) {
				LOG.debug("Parsing from index {}/{}", in.readerIndex(), in.readableBytes());
	
				LOG.debug("trying to decode {}", new String(UnpooledByteBufAllocator.DEFAULT.heapBuffer().writeBytes(in, in.readerIndex(), in.readableBytes()).array()));
				
				// so, there is at least one delimiter here, but do we have attribute length + 2 more bytes to read?
				byte[] attrLength = this.in.readBytes( this.in.forEachByte(ByteBufProcessor.FIND_CRLF) - this.in.readerIndex() ).array();
				currentBytesExpected = Integer.valueOf( new String(attrLength) ); 
			}
			LOG.debug("bytesExpected {}", currentBytesExpected);
			
			if (currentBytesExpected == -1) {
				// no result
				currentBytesExpected = 0;

				setValue(null);
				return true; // done with this response
			} else if (this.in.readableBytes() >= currentBytesExpected+4) { // there should be 2x delimiters in here, plus the content
				// we have the remainder of the response in this buffer. Finish reading.
				byte[] attribute = this.in.readBytes(currentBytesExpected+2).array();
				currentBytesExpected = 0;

				setValue(new String(attribute));
				return true; // done with this response
			}
			// expected response length isnt -1 and buffer contains fewer bytes. Wait for another invocation of decode() 
			
			LOG.debug("Waiting for more data in the buffer");
			
		}
		
		return false;
	}

}
