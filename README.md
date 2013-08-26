***Attention***

This is not feature-complete, it will be in 1.0, Until then, refer to the CHANGELOG below to find out if it's useful for you or not. There are basically no unit tests or documentation.

# MTGC Redis-Client

..is a netty4/disruptor backed Redis client made for high availability, low  latency and ease of use.

## Design goals


- **High availability**: extendible monitoring interface - ships with "connection recovery" & "mbeans-exported client health & stats" impementations. Quickly recover from losing connections and keep you and your monitoring systems up to date on the state of the client.
- **Low latency**: makes use of the LMAX disruptor to paralellize pre-processing on Requests and present a single-writer to the nio client Channel
- **Ease of use**: Public Fluent APIs wherever possible


## CHANGELOG

### 0.1-SNAPSHOT

[21/08/2013]

- some connection state management ( will try to re-establish connections )
- significant bug fixes
- separated out Disruptor to its own Client impl. Need to see if there is a real benefit vs going directly to the Netty Channel through Client

[19/08/2013]


- requests supported: Set, Setx, Setnx, Exists, Get, Watch, Multi, Exec, Ping
- Transaction support: a sequence of requests sent as a single request on the same connection ( pin-able, see below ), prefixed automatically with MULTI and EXEC
- no connection-state management ( needs to be implemented in client.monitor.SelfHealingMonitor )
- little Javadoc
- rudimentary tests

## Usage
```
// Create a Client fronted by a RingBuffer & 2 processors ( hashing, routing )
Client client = new DisruptorClient.Builder()
				.withProcessingBufferSize(1024)
				.withProcessingWaitStrategy(new BlockingWaitStrategy())
				.addHost("127.0.0.1", 6379)
				.addHost("127.0.0.1", 6380)
				.addMonitor(new LoggingMonitor())
				.withTrafficLogging(true)
				.shouldHash(true)
				.withHashFunction(Hashing.murmur3_128())
				.withConnectionsPerHost(4)
				.withTrafficLogging(true)
				.build();
				

				
// Connect it up
client.connect();

// Send a request
client.send(new Set("Foo", "Bar"), new Runnable() {

	public void run() {
		String v = client.send(new Get("Foo")).get(5, TimeUnit.MILLISECONDS);
		
		client.send(new Watch("Foo")); // single-keyed commands are automatically hashed
		
		List<Response> r = client.send(new Transaction()
					.pin(client.hostForKey("Foo"))
					.add(new Setex("Foo", v+" ?!", 3600)))).get(10, TimeUnit.MILLISECONDS)
					
		if (r.size() == 0) System.err.println("Transaction aborted!")
					
	}

}).get(10, TimeUnit.MILLISECONDS);



				
```

