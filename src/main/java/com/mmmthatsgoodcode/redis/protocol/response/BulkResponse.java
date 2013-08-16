package com.mmmthatsgoodcode.redis.protocol.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;

import com.mmmthatsgoodcode.redis.protocol.Response;

/**
 * This should contain a single, arbitary-length incoming parameter as specified by ${length}DELIMITER
 * @author aszerdahelyi
 *
 */
public class BulkResponse extends Response<String> {

	private static final Logger LOG = LoggerFactory.getLogger(BulkResponse.class);
	
	public BulkResponse(ByteBuf in) {
		super(in);
	}

	/**
	 * Expected format:
	 * ${attribute length}DELIMITER{attribute}DELIMITER
	 */
	@Override
	public boolean decode() {
		if (this.in.forEachByte(ByteBufProcessor.FIND_CRLF) != -1) {
			LOG.debug("Parsing from index {}", in.readerIndex());
			// so, there is at least one delimiter here, but do we have attribute length + 2 more bytes to read?
			byte[] attrLength = this.in.readBytes( this.in.forEachByte(ByteBufProcessor.FIND_CRLF) - this.in.readerIndex() ).array();
			int bytesExpected = Integer.valueOf( new String(attrLength) ); 
			if (this.in.readableBytes() >= bytesExpected+4) { // there should be 2x delimiters in here, plus the content
				// we're cool, lets read on 
				this.in.readerIndex(this.in.readerIndex()+2);
				byte[] attribute = this.in.readBytes(bytesExpected).array();
				
				setValue(new String(attribute));
				
				return true;
			}
			
		}
		
		return false;
	}

}
