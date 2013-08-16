package com.mmmthatsgoodcode.redis.disruptor.processor;

import com.google.common.hash.HashCode;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventTranslator;
import com.mmmthatsgoodcode.redis.protocol.Request;

public class RequestEvent {

			
	public static class KeyedRequestEventFactory implements EventFactory<RequestEvent> {

		@Override
		public RequestEvent newInstance() {
			return new RequestEvent();
		}
		
	}
	
	public static class RequestEventTranslator implements EventTranslator<RequestEvent> {

		private final Request request;
		
		public RequestEventTranslator(Request request) {
			this.request = request;
		}
			
		@Override
		public void translateTo(RequestEvent event, long sequence) {
			event.place(request);
		}
		
	}
	
	public static final EventFactory<RequestEvent> EVENT_FACTORY = new KeyedRequestEventFactory();
	private Request request = null;
	private HashCode hash;
	
	public void place(Request request) {
		this.request = request;
	}
	
	public Request getRequest() {
		return request;
	}
	
	public void setHash(HashCode hash) {
		this.hash = hash;
	}
	
	public HashCode getHash() {
		return hash;
	}

	
	public String toString() {
		return request.getClass().getSimpleName()+"#"+request;
	}
	
}
