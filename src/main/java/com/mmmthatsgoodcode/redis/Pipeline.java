package com.mmmthatsgoodcode.redis;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

import com.mmmthatsgoodcode.redis.protocol.Request;

public class Pipeline {

	private HashMap<Client, ArrayBlockingQueue<Request>> clients = new HashMap<Client, ArrayBlockingQueue<Request>>();
	
	
}
