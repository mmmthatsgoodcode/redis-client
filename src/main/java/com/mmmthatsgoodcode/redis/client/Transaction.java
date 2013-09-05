package com.mmmthatsgoodcode.redis.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import io.netty.buffer.ByteBuf;

import com.mmmthatsgoodcode.redis.Host;
import com.mmmthatsgoodcode.redis.protocol.PendingReply;
import com.mmmthatsgoodcode.redis.protocol.PinnedCommand;
import com.mmmthatsgoodcode.redis.protocol.AbstractCommand;
import com.mmmthatsgoodcode.redis.protocol.Command;
import com.mmmthatsgoodcode.redis.protocol.command.Multi;
import com.mmmthatsgoodcode.redis.protocol.reply.MultiBulkReply;

public class Transaction extends AbstractCommand<MultiBulkReply> implements PinnedCommand<MultiBulkReply>, Iterable<AbstractCommand> {

	private Host host;
	private List<AbstractCommand> commands = new ArrayList<AbstractCommand>();
	
	public Transaction() {
		commands.add(new Multi());
	}
	
	public Transaction(AbstractCommand...pre) {
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
	public Transaction add(AbstractCommand command) {
		commands.add(command);
		
		return this;
	}
	
	public Transaction add(AbstractCommand...commands) {
		for(AbstractCommand command:commands) {
			add(command);
		}
		
		return this;
	}
	
	@Override
	public ByteBuf encode() {
		
		ByteBuf out = byteBufAllocator.buffer();
		
		for (Command command:this) {
			ByteBuf rbuff = command.encode();
			out.writeBytes(rbuff);
			rbuff.release();
		}
		
		return out;
		
	}

	@Override
	public byte[] getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Host getHost() {
		return host;
	}

	public List<AbstractCommand> getCommands() {
		return commands;
	}

	@Override
	public Iterator<AbstractCommand> iterator() {
		return commands.iterator();
	}
	
	public String toString() {
		
		return getClass().getSimpleName()+"#"+hashCode()+"("+commands+")";
		
	}


}
