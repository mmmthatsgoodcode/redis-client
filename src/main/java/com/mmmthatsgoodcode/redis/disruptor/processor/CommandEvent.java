package com.mmmthatsgoodcode.redis.disruptor.processor;

import com.google.common.hash.HashCode;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventTranslator;
import com.mmmthatsgoodcode.redis.protocol.Command;

public class CommandEvent {

			
	public static class CommandEventFactory implements EventFactory<CommandEvent> {

		@Override
		public CommandEvent newInstance() {
			return new CommandEvent();
		}
		
	}
	
	public static class CommandEventTranslator implements EventTranslator<CommandEvent> {

		private final Command command;
		
		public CommandEventTranslator(Command command) {
			this.command = command;
		}
			
		@Override
		public void translateTo(CommandEvent event, long sequence) {
			event.place(command);
		}
		
	}
	
	public static final EventFactory<CommandEvent> EVENT_FACTORY = new CommandEventFactory();
	private Command command = null;
	private HashCode hash = null;
	
	public void place(Command command) {
		this.command = command;
	}
	
	public Command getCommand() {
		return command;
	}
	
	public void setHash(HashCode hash) {
		this.hash = hash;
	}
	
	public HashCode getHash() {
		return hash;
	}

	
	public String toString() {
		return command.getClass().getSimpleName()+"#"+command;
	}
	
}
