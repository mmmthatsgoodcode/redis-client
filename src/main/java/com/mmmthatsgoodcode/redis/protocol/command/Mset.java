package com.mmmthatsgoodcode.redis.protocol.command;

import java.util.HashMap;
import java.util.Map;

import com.mmmthatsgoodcode.redis.protocol.model.AbstractCommand;
import com.mmmthatsgoodcode.redis.protocol.reply.StatusReply;

public class Mset extends AbstractCommand<StatusReply>{

	private final Map<String,String> keyValueMap = new HashMap<String, String>();
	
	public Mset(String key, String value){
		this.keyValueMap.put(key, value);
	}
	
	public Mset(String key, String value, String... keysValues){
		this.keyValueMap.put(key, value);
		for(int i=0; i<keysValues.length;i +=2 ){
			this.keyValueMap.put(keysValues[i], keysValues[i+1]);
		}
	}
	
	public Map<String, String> getKeyValueMap() {
		return keyValueMap;
	}
}
