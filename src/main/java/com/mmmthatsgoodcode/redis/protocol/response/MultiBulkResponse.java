package com.mmmthatsgoodcode.redis.protocol.response;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;

import com.mmmthatsgoodcode.redis.protocol.AbstractResponse;
import com.mmmthatsgoodcode.redis.protocol.Response;

public class MultiBulkResponse extends AbstractResponse<List<Response>> {
	
	private static final Logger LOG = LoggerFactory.getLogger(MultiBulkResponse.class);
	private List<Response> responses = new ArrayList<Response>();
	private int paramLength = 0;
	
	public MultiBulkResponse(ByteBuf in) {
		super(in);
	}
	/**
	 * Expected format:
	 * *{number of bulk replies expected}CR+LF
	 * ${attribute length}CR+LF{attribute}CR+LF
	 * .
	 * .
	 * ${attribute length}CR+LF{attribute}CR+LF
	 */
	@Override
	public boolean decode() {
		
		// wait for a delimiter to come
		if (this.in.forEachByte(HAS_DELIMITER) != -1) {
			
			// read parameter count if we haven't already
			if (paramLength == 0) {
				byte[] paramLengthBytes = this.in.readBytes( this.in.forEachByte(HAS_DELIMITER) - this.in.readerIndex() ).array();
				paramLength = Integer.valueOf( new String(paramLengthBytes, ENCODING) );
				if (paramLength == -1) {
					LOG.debug("Null MultiBulk reply!");
					setValue(responses);
					return true;
				}
				LOG.debug("Expecting {} Responses", paramLength);
				this.in.readerIndex(this.in.readerIndex()+2); // move reader index beyond the CRLF

			}
			
			if (responses.size() == 0 && this.in.readableBytes() > 1) responses.add(AbstractResponse.infer(in));
			
			while (responses.size() > 0 && responses.size() <= paramLength) {
				LOG.debug("Decoding Response {}, {} in MultiBulkResponse", responses.size()+" of "+paramLength, responses.get(responses.size()-1).getClass().getSimpleName());

				if (responses.get(responses.size()-1).decode() == true) {

					// see if we need to prepare the next response
					if (responses.size() != paramLength) {
						this.in.readerIndex(this.in.readerIndex()+DELIMITER.length); // move reader index beyond the CRLF
						if (this.in.readableBytes() > 1) responses.add(AbstractResponse.infer(in)); // infer next response
						else return false; // not enough bytes in buffer to infer the next response.. wait for more.

					} else {
						
						// done!
						setValue(responses);
						return true;
					}
					
				} else {
					// there wasnt enough data in here for the bulk reply to decode. 
					LOG.debug("Waiting for more data");
					return false;
				}
								
			}
			
		}
		
		
		return false;
	
	}
	
	
}
