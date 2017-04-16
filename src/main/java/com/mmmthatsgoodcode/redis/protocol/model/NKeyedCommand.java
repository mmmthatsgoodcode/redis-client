package com.mmmthatsgoodcode.redis.protocol.model;

import java.util.Arrays;
import java.util.List;

import com.mmmthatsgoodcode.redis.protocol.Reply;

public abstract class NKeyedCommand<T extends Reply> extends AbstractCommand<T> {

	protected final List<String> keys;
	
	public NKeyedCommand(String... keys){
		this.keys = Arrays.asList(keys);
	}
	
	public List<String> getKeys(){
		return this.keys;
	}
}
