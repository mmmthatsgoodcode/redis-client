package com.mmmthatsgoodcode.redis;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.LifecycleAware;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.Sequencer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.WorkProcessor;
import com.mmmthatsgoodcode.redis.client.ClientWriteHandler;
import com.mmmthatsgoodcode.redis.client.RequestEncoder;
import com.mmmthatsgoodcode.redis.client.RequestLogger;
import com.mmmthatsgoodcode.redis.client.ResponseDecoder;
import com.mmmthatsgoodcode.redis.disruptor.processor.RequestEvent;
import com.mmmthatsgoodcode.redis.protocol.Request;

/**
 * Represents a single connection a RedisHost.
 * Buffers incoming requests on a RingBuffer
 * @author andras
 *
 */
public class Connection  {
	
	/**
	 * Polls the ring buffer and sends the request up the Netty pipeline
	 * @author andras
	 *
	 */
	private class RequestProcessor implements WorkHandler<RequestEvent>, LifecycleAware {

		private final String name;
		private final Logger LOG;

		public RequestProcessor(String name) {
			this.name = name;
			LOG = LoggerFactory.getLogger(this.getClass());
			LOG.debug("Created!");
		}
		
		public String toString() {
			return name;
		}


		@Override
		public void onEvent(RequestEvent event) throws Exception {
			LOG.debug("Received Request {} for processing on {}", event.getRequest(), Connection.this.channel);
			Connection.this.channel.writeAndFlush(event.getRequest()).syncUninterruptibly();
						
		}

		@Override
		public void onStart() {
			LOG.debug("Started!");
		}

		@Override
		public void onShutdown() {
			LOG.debug("Shut down!");
			
		}
		
	}
	
	private class RequestExceptionHandler implements ExceptionHandler {

		private final Logger LOG = LoggerFactory.getLogger(RequestExceptionHandler.class);
		
		@Override
		public void handleEventException(Throwable ex, long sequence,
				Object event) {
			LOG.error("Event exception {}", ex);
			
		}

		@Override
		public void handleOnStartException(Throwable ex) {
			LOG.error("OnStart exception {}", ex);
		}

		@Override
		public void handleOnShutdownException(Throwable ex) {
			LOG.error("OnShutdown exception {}", ex);			
		}
		
	}
	
	public enum State { CREATED, CONNECTING, CONNECTED, DISCONNECTED }

	protected volatile Connection.State state = State.CREATED;		
	private Channel channel = null;
	private Bootstrap bootstrap = new Bootstrap();
	private final Host host;
	private final RingBuffer<RequestEvent> sendBuffer;
	private static final Logger LOG = LoggerFactory.getLogger(Connection.class);
	protected ExecutorService processors = Executors.newFixedThreadPool(1);

	public Connection(Host host, WaitStrategy sendWaitStrategy, int bufferSize) {
		
		this.host = host;
		
		bootstrap.group(new NioEventLoopGroup());
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 100);
		bootstrap.handler(new ChannelInitializer<SocketChannel> () {

			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				
//				ch.pipeline().addLast(new RequestLogger(), new RequestEncoder(), new ClientWriteHandler(), new ResponseDecoder());
				ch.pipeline().addLast(new RequestEncoder(), new ClientWriteHandler(), new ResponseDecoder());

			}
			
			
		});
		
		// create this connections outbound request buffer
		sendBuffer = RingBuffer.createMultiProducer(RequestEvent.EVENT_FACTORY, bufferSize, sendWaitStrategy);
		
		LOG.debug("Connection object created");
		
		
	}
	
	/**
	 * Add Request to the send buffer
	 * @param request
	 * @return
	 */
	public Connection schedule(Request request) {
		sendBuffer.publishEvent(new RequestEvent.RequestEventTranslator(request));

		LOG.debug("Sent Request {} to ringbuffer", request);
		System.out.println( sendBuffer.remainingCapacity() );

		return this;
	}
	
	public synchronized Connection connect() {
		
		if (channel == null) {
			
			setState(State.CONNECTING, null);
			
			ChannelFuture cFuture = bootstrap.connect(host.getHostInfo().getHostname(), host.getHostInfo().getPort()).addListener(new ChannelFutureListener() {
				
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if (future.isSuccess()) setState(State.CONNECTED, null);
					else setState(State.DISCONNECTED, null);
				}
				
			});
			
			cFuture.syncUninterruptibly();
			channel = cFuture.channel();
						
			// create and start the request processor for this connection
			String processorName = "Processor-"+host+"#"+hashCode();
			WorkProcessor<RequestEvent> sender = new WorkProcessor<RequestEvent>(sendBuffer, sendBuffer.newBarrier(), new RequestProcessor(processorName), new RequestExceptionHandler(), new Sequence(Sequencer.INITIAL_CURSOR_VALUE));
			sendBuffer.addGatingSequences(sender.getSequence());

			processors.execute(sender);
			
		}
		
		return this;
	}
	
	private void setState(Connection.State newState, Throwable cause) {
		for (ClientMonitor monitor:host.getClient().getMonitors()) {
			
			if (state == State.CONNECTING && newState == State.DISCONNECTED) monitor.connectionFailed(this, cause);
			if (state == State.CONNECTED && newState == State.DISCONNECTED) monitor.connectionLost(this, cause);
			if (newState == State.CREATED) monitor.connectionCreated(this);
			if (newState == State.CONNECTING) monitor.connectionInProgress(this);
			if (newState == State.CONNECTED) monitor.connected(this);
			
		}
		
		state = newState;

	}
	
	public State getState() {
		return state;
	}


}