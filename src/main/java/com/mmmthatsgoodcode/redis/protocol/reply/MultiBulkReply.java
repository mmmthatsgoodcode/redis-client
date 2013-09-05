package com.mmmthatsgoodcode.redis.protocol.reply;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;

import com.mmmthatsgoodcode.redis.protocol.AbstractReply;
import com.mmmthatsgoodcode.redis.protocol.Reply;

public class MultiBulkReply extends AbstractReply<List<Reply>> {
	
	private static final Logger LOG = LoggerFactory.getLogger(MultiBulkReply.class);
	private List<Reply> replies = new ArrayList<Reply>();
	private int paramLength = 0;
	
	public MultiBulkReply(ByteBuf in) {
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
					setValue(replies);
					return true;
				}
				LOG.debug("Expecting {} Replies", paramLength);
				this.in.readerIndex(this.in.readerIndex()+2); // move reader index beyond the CRLF

			}
			
			if (replies.size() == 0 && this.in.readableBytes() > 1) replies.add(AbstractReply.infer(in));
			
			while (replies.size() > 0 && replies.size() <= paramLength) {
				LOG.debug("Decoding Reply {}, {} in MultiBulkReply", replies.size()+" of "+paramLength, replies.get(replies.size()-1).getClass().getSimpleName());

				if (replies.get(replies.size()-1).decode() == true) {

					// see if we need to prepare the next reply
					if (replies.size() != paramLength) {
						this.in.readerIndex(this.in.readerIndex()+DELIMITER.length); // move reader index beyond the CRLF
						if (this.in.readableBytes() > 1) replies.add(AbstractReply.infer(in)); // infer next reply
						else return false; // not enough bytes in buffer to infer the next reply.. wait for more.

					} else {
						
						// done!
						setValue(replies);
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
