package com.mmmthatsgoodcode.redis.protocol.command;

import java.util.Arrays;
import java.util.List;

import com.mmmthatsgoodcode.redis.Protocol.CommandType;
import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.MultiBulkReply;

public class Commandinfo extends AbstractCommand<MultiBulkReply>{

	private final List<byte[]> commandList;
	
	public Commandinfo(byte[]... commands){
		commandList = Arrays.asList(commands);
	}
	
	public List<byte[]> getCommandList() {
		return commandList;
	}
}
