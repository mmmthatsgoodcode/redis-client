package com.mmmthatsgoodcode.redis.protocol.request;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import io.netty.buffer.ByteBuf;

import com.mmmthatsgoodcode.redis.Host;
import com.mmmthatsgoodcode.redis.protocol.KeyedRequest;
import com.mmmthatsgoodcode.redis.protocol.PinnedRequest;
import com.mmmthatsgoodcode.redis.protocol.Request;
import com.mmmthatsgoodcode.redis.protocol.Request.EncodeHelper;
import com.mmmthatsgoodcode.redis.protocol.response.StatusResponse;

public class Watch extends Request<StatusResponse> implements PinnedRequest {

	private static final byte[] NAME = "WATCH".getBytes(ENCODING);
	private final String[] keys;
	private Host host = null;
	
	public Watch(String...keys) {
		this.keys = keys;
		setArgc(1+this.keys.length);
	}

	@Override
	public ByteBuf encode() {
		EncodeHelper out = new EncodeHelper(byteBufAllocator.buffer());
		out.addArg(getName());
		for(String key:keys) {
			out.addArg(key.getBytes(ENCODING));
		}
		return out.buffer();		
	}

	@Override
	public byte[] getName() {
		return NAME;
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

}
