package com.mmmthatsgoodcode.redis.protocol.command;

import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.MultiBulkReply;

// Return value type not explained in Doc, assumed to be MultiBulkReply
public class Slowlog extends AbstractCommand<MultiBulkReply>{

	private final byte[] subcommand;
	private final byte[] argument;
	
	public Slowlog (byte[] subcommand){
		this.subcommand = subcommand;
		this.argument = null;
	}
	
	public Slowlog (byte[] subcommand, byte[] argument) {
		this.subcommand = subcommand;
		this.argument = argument;
	}
	
	public byte[] getSubcommand() {
		return subcommand;
	}
	
	public byte[] getArgument() {
		return argument;
	}
}