package com.mmmthatsgoodcode.redis.protocol.command;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import io.netty.buffer.ByteBuf;

import com.mmmthatsgoodcode.redis.Host;
import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand;
import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.model.PinnedCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.StatusReply;

public class Watch extends AbstractCommand<StatusReply> implements PinnedCommand<StatusReply> {

	private final List<String> keys;
	private Host host = null;
	
	public Watch(String...keys) {
		this.keys = Arrays.asList(keys);
	}

	@Override
	public Watch pin(Host host) {
		this.host = host;
		return this;
	}

	@Override
	public Host getHost() {
		return host;
	}

	public List<String> getKeys() {
		return keys;
	}
}
