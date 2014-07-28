package com.mmmthatsgoodcode.redis.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import io.netty.buffer.ByteBuf;

import com.mmmthatsgoodcode.redis.Host;
import com.mmmthatsgoodcode.redis.protocol.Command;
import com.mmmthatsgoodcode.redis.protocol.command.Multi;
import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand;
import com.mmmthatsgoodcode.redis.protocol.model.PendingReply;
import com.mmmthatsgoodcode.redis.protocol.model.PinnedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.MultiBulkReply;

public class Transaction extends AbstractCommand<MultiBulkReply> implements PinnedCommand<MultiBulkReply>, Iterable<Command> {

	private Host host;
	private List<Command> commands = new ArrayList<Command>();
	
	public Transaction() {
		commands.add(new Multi());
	}
	
	public Transaction(Command...pre) {
		commands.addAll(Arrays.asList(pre));
		commands.add(new Multi());
	}
	
	public Transaction pin(Host host) {
		this.host = host;
		return this;
	}
	
	/**
	 * TODO enforce command type limitations
	 * @param command
	 * @return
	 */
	public Transaction add(Command command) {
		commands.add(command);
		
		return this;
	}
	
	public Transaction add(Command...commands) {
		for(Command command:commands) {
			add(command);
		}
		
		return this;
	}

	@Override
	public Host getHost() {
		return host;
	}

	public List<Command> getCommands() {
		return commands;
	}

	@Override
	public Iterator<Command> iterator() {
		return commands.iterator();
	}
	
	public String toString() {
		
		return getClass().getSimpleName()+"#"+hashCode()+"("+commands+")";
		
	}


}
