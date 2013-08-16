package com.mmmthatsgoodcode.redis.protocol;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;


public class Pipeline extends Request implements Iterable<Request> {

	private List<Request> requests = new ArrayList<Request>();
	
	public Pipeline() {

	}
	
	public Pipeline add(Request request) {
		requests.add(request);
		return this;
	}
	
	@Override
	public ByteBuf encode() {
		EncodeHelper out = new EncodeHelper(byteBufAllocator.buffer());
		setArgc(0);

		for(Request request:this) {
//			setArgc(getArgc()+request.getArgc());
			out.addArgc(request.getArgc());
			out.buffer().writeBytes(request.encode());
		}
		
		return out.buffer();
		
	}
	@Override
	public Iterator<Request> iterator() {
		return requests.iterator();
	}

	public int size() {
		return requests.size();
	}
	
	@Override
	public byte[] getName() {
		return null;
	}

	public String toString() {
		return "Pipelined("+this.requests.size()+"):"+requests;
	}

	@Override
	public boolean canPipe() {
		return false; // obviously.
	}
	
	
}
