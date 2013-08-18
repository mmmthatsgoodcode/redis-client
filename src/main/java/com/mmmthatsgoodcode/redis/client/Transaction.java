package com.mmmthatsgoodcode.redis.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import io.netty.buffer.ByteBuf;

import com.mmmthatsgoodcode.redis.Host;
import com.mmmthatsgoodcode.redis.protocol.PendingResponse;
import com.mmmthatsgoodcode.redis.protocol.PinnedRequest;
import com.mmmthatsgoodcode.redis.protocol.Request;
import com.mmmthatsgoodcode.redis.protocol.request.Multi;
import com.mmmthatsgoodcode.redis.protocol.response.MultiBulkResponse;

public class Transaction extends Request<MultiBulkResponse> implements PinnedRequest, Iterable<Request> {

	private Host host;
	private List<Request> requests = new ArrayList<Request>();
	
	public Transaction() {
		requests.add(new Multi());
	}
	
	public Transaction(Request...pre) {
		requests.addAll(Arrays.asList(pre));
		requests.add(new Multi());
	}
	
	public Transaction pin(Host host) {
		this.host = host;
		return this;
	}
	
	/**
	 * TODO enforce request type limitations
	 * @param request
	 * @return
	 */
	public Transaction add(Request request) {
		requests.add(request);
		
		return this;
	}
	
	public Transaction add(Request...requests) {
		for(Request request:requests) {
			add(request);
		}
		
		return this;
	}
	
	@Override
	public ByteBuf encode() {
		
		ByteBuf out = byteBufAllocator.buffer();
		
		for (Request request:this) {
			ByteBuf rbuff = request.encode();
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

	public List<Request> getRequests() {
		return requests;
	}

	@Override
	public Iterator<Request> iterator() {
		return requests.iterator();
	}
	
	public String toString() {
		
		return getClass().getSimpleName()+"#"+hashCode()+"("+requests+")";
		
	}


}
