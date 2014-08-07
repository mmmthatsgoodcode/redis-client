package com.mmmthatsgoodcode.redis.protocol.model;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;

import com.mmmthatsgoodcode.redis.Protocol;
import com.mmmthatsgoodcode.redis.protocol.Command;
import com.mmmthatsgoodcode.redis.protocol.Reply;

public abstract class AbstractCommand<T extends Reply> implements Command<T> {
	
	protected PendingReply<T> reply = new PendingReply<T>(this);

	
	/* (non-Javadoc)
	 * @see com.mmmthatsgoodcode.redis.protocol.CommandInterface#replyReceived(T)
	 */
	@Override
	public void replyReceived(T reply) {
		reply.setCommand(this);
	}

	/* (non-Javadoc)
	 * @see com.mmmthatsgoodcode.redis.protocol.CommandInterface#getReply()
	 */
	@Override
	public PendingReply<T> getReply() {
		return this.reply;
	}
	
	
}
