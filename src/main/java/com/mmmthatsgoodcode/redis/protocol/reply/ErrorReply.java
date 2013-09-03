package com.mmmthatsgoodcode.redis.protocol.reply;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;

import com.mmmthatsgoodcode.redis.protocol.model.AbstractReply;

public class ErrorReply extends AbstractReply<String> {

	private final String errorType, errorMessage;
	
	public ErrorReply(String errorType, String errorMessage) {
		this.errorType = errorType;
		this.errorMessage = errorMessage;
		setValue(this.errorType+": "+this.errorMessage);
	}

	public String getErrorType() {
		return errorType;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public boolean equals(Object object) {
		if (!(object instanceof ErrorReply)) return false;
		ErrorReply other = (ErrorReply) object;

		return other.getErrorMessage().equals(getErrorMessage()) && other.getErrorType().equals(getErrorType());
		
 	}
	
}
