package com.mmmthatsgoodcode.redis;

import io.netty.channel.EventLoopGroup;

import java.util.List;

import com.google.common.hash.HashCode;
import com.mmmthatsgoodcode.redis.client.NoConnectionsAvailableException;
import com.mmmthatsgoodcode.redis.client.Transaction;
import com.mmmthatsgoodcode.redis.protocol.Command;
import com.mmmthatsgoodcode.redis.protocol.Reply;
import com.mmmthatsgoodcode.redis.protocol.model.KeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.model.MultiKeyedCommand;
import com.mmmthatsgoodcode.redis.protocol.model.PendingReply;
import com.mmmthatsgoodcode.redis.protocol.model.PinnedCommand;
import com.mmmthatsgoodcode.redis.protocol.model.SplittableCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.MultiBulkReply;

public interface Client {

	public abstract List<ClientMonitor> getMonitors();

	public abstract List<Host> getHosts();

	public abstract void connect();

	public abstract PendingReply<MultiBulkReply> send(Transaction transaction)
			throws NoConnectionsAvailableException;

	public abstract <T extends Reply> PendingReply<T> send(
			KeyedCommand<T> keyedCommand)
			throws NoConnectionsAvailableException;

	public abstract <T extends Reply> PendingReply<T> send(
			PinnedCommand<T> pinnedCommand)
			throws NoConnectionsAvailableException;

	public abstract <C extends SplittableCommand, T extends Reply> PendingReply<T> send(SplittableCommand<C, T> command)
			throws NoConnectionsAvailableException;

	public abstract <T extends Reply> PendingReply<T> send(Command<T> command)
			throws NoConnectionsAvailableException;
	

	public abstract Host hostForKey(String key);

	public abstract HashCode hashForKey(String key);

	public abstract Host hostForHash(HashCode hash);

	public abstract boolean shouldHash();

	public abstract boolean trafficLogging();

	public abstract Protocol getProtocol();

	public abstract EventLoopGroup getEventLoopGroup();
}