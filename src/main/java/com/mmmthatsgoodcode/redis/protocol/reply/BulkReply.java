package com.mmmthatsgoodcode.redis.protocol.reply;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;
import io.netty.buffer.UnpooledByteBufAllocator;

import com.mmmthatsgoodcode.redis.protocol.AbstractReply;

/**
 * This should contain a single, arbitary-length incoming parameter as specified by ${length}DELIMITER
 * @author aszerdahelyi
 *
 */
public class BulkReply extends AbstractReply<String> {

	private static final Logger LOG = LoggerFactory.getLogger(BulkReply.class);
	private int currentBytesExpected = 0; // how many bytes we are waiting to become available in this buffer
	
	public BulkReply(ByteBuf in) {
		super(in);
	}

	/**
	 * Expected format:
	 * ${attribute length}CR+LF{attribute}CR+LF
	 */
	@Override
	public boolean decode() {
		
		// wait for a delimiter to come
		if (this.in.forEachByte(ByteBufProcessor.FIND_CRLF) != -1) {
			
			if (currentBytesExpected == 0) {
				LOG.debug("Starting from index {} ( {} readable )", in.readerIndex(), in.readableBytes());
	
//				LOG.debug("trying to decode {}", new String(UnpooledByteBufAllocator.DEFAULT.heapBuffer().writeBytes(in, in.readerIndex(), in.readableBytes()).array()));
				
				// so, there is at least one delimiter here, but do we have attribute length + 2 more bytes to read?
				byte[] attrLength = this.in.readBytes( this.in.forEachByte(HAS_DELIMITER) - this.in.readerIndex() ).array();
				currentBytesExpected = Integer.valueOf( new String(attrLength) ); 
			}
			LOG.debug("Expecting {} bytes", currentBytesExpected);
			
			if (currentBytesExpected == -1) {
				// no result
				currentBytesExpected = 0;

				setValue(null);
				return true; // done with this reply
			} else if (this.in.readableBytes() >= currentBytesExpected+(DELIMITER.length*2)) { // there should be 2x delimiters in here, plus the content
				LOG.debug("There are sufficient bytes in this buffer to finish decoding");
				// we have the remainder of the reply in this buffer. Finish reading.
				this.in.readerIndex(this.in.readerIndex()+DELIMITER.length); // move reader index beyond the CRLF
				byte[] attribute = this.in.readBytes(currentBytesExpected).array();
				currentBytesExpected = 0;

				setValue(new String(attribute));
				return true; // done with this reply
			}
			// expected reply length isnt -1 and buffer contains fewer bytes. Wait for another invocation of decode() 
			
			LOG.debug("Waiting for more data in the buffer");
			
		}
		
		return false;
	}

}
