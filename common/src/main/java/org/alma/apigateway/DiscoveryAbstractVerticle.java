package org.alma.apigateway;

import java.util.Set;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.impl.ConcurrentHashSet;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.servicediscovery.types.HttpEndpoint;

public class DiscoveryAbstractVerticle extends AbstractVerticle {

	private static final Logger logger = LoggerFactory.getLogger(DiscoveryAbstractVerticle.class);

	protected ServiceDiscovery discovery;
	protected Set<Record> registeredRecords = new ConcurrentHashSet<>();
	@Override
	public void start(Future<Void> startFuture) throws Exception {
		 // init service discovery instance
	    discovery = ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions().setBackendConfiguration(config()));
	
	}

	/**
	 * Publish en Http endpoint into the service discovery
	 * Needs to have a config.json file with a field api.name.
	 * Example : { ... "api.name" : "product",...}
	 * @param name
	 * @param host
	 * @param port
	 * @return
	 */
	protected Future<Void> publishHttpEndpoint(String name, String host, int port) {
		// create a record with a name, a host and a port
		Record record = HttpEndpoint.createRecord(name, host, port, "/",
	    		new JsonObject().put("api.name", config().getString("api.name", ""))
	    );
	    return publish(record);
	  }
	
	  /**
	   * Publish a service with record.
	   *
	   * @param record service record
	   * @return async result
	   */
	  private Future<Void> publish(Record record) {
	    if (discovery == null) {
	      try {
	        start();
	      } catch (Exception e) {
	        throw new IllegalStateException("Cannot create discovery service");
	      }
	    }

	    Future<Void> future = Future.future();
	    // publish the service
	    discovery.publish(record, ar -> {
	      if (ar.succeeded()) {
	        registeredRecords.add(record);
	        logger.info("Service <" + ar.result().getName() + "> published");
	        
	        future.complete();
	      } else {
	        future.fail(ar.cause());
	      }
	    });

	    return future;
	  }
}
