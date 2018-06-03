package org.alma.apigateway;

import java.util.List;
import java.util.Optional;

import io.vertx.core.Future;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.HttpEndpoint;

public class ApiGatewayVerticle extends RestApiVerticle {

	  private static final String SERVICE_NAME = "gateway-api";/**
	/**
	 * Définition du Logger
	 */
	  private static final Logger logger = LoggerFactory.getLogger(ApiGatewayVerticle.class);
	@Override
	public void start(Future<Void> startFuture) throws Exception {
		
		  
		super.start(startFuture);
		logger.info("Démarrage de l'API gateway");
		// définition du router
	    Router router = Router.router(vertx); 
	    // body handler
	    router.route().handler(BodyHandler.create());
	    // api dispatcher
	    router.route("/api/*").handler(this::dispatchRequests);

	    // get HTTP host and port from configuration, or use default value
	    String host = config().getString("gateway.http.address", "0.0.0.0");
	    int port = config().getInteger("gateway.http.port", 8086);
	    String apiname = config().getString("api.name");
	    logger.info("gateway apiname:" + apiname+" host :" + host + ", port :"+port);
	    logger.info("config "+config());
	    createHttpServer(router, host, port)
	      .compose(serverCreated -> publishHttpEndpoint(SERVICE_NAME, host, port))
	      .setHandler(startFuture.completer());
	}
	
	private void dispatchRequests(RoutingContext context) {
	logger.info("Appel de  gateway");
		int initialOffset = 5; // length of `/api/`
		 Future<Object> data = Future.future();
		
		 data.setHandler(ar -> {
			   if (ar.failed()) {
				   logger.info("Appel plantée de  gateway");
			      }
			   else {
				   logger.info("Appel réussie de  gateway");
			   }
		 });
		 
	     getAllEndpoints().setHandler(ar -> {
	         if (ar.succeeded()) {
	           List<Record> recordList = ar.result();
	           // get relative path and retrieve prefix to dispatch client
	           String path = context.request().uri();

	           String prefix = (path.substring(initialOffset)
	             .split("/"))[0];
	           logger.info(prefix);
	           // generate new relative path
	           String newPath = path.substring(initialOffset + prefix.length());
	           // get one relevant HTTP client, may not exist
	           Optional<Record> client = recordList.stream()
	             .filter(record -> record.getMetadata().getString("api.name") != null)
	             .filter(record -> record.getMetadata().getString("api.name").equals(prefix))
	             .findAny(); // simple load balance

	           if (client.isPresent()) {
	            logger.info(newPath + " find into the discovery");
	        	 doDispatch(context, newPath, discovery.getReference(client.get()).get(), data);
	           } else {
	        	   logger.info("not find into the discovery");
	          //
	           }
	         } else {
	        	   logger.info("not find into the discovery");
	          // future.fail(ar.cause());
	         }
	       });
		
	   
	}
	 private Future<List<Record>> getAllEndpoints() {
		    Future<List<Record>> future = Future.future();
		    discovery.getRecords(record -> record.getType().equals(HttpEndpoint.TYPE),
		      future.completer());
		    return future;
		  }
	  /**
	   * Dispatch the request to the downstream REST layers.
	   *
	   * @param context routing context instance
	   * @param path    relative path
	   * @param client  relevant HTTP client
	   */
	  private void doDispatch(RoutingContext context, String path, HttpClient client, Future<Object> cbFuture) {
		  logger.info("client :"+client.toString());
		  HttpClientRequest toReq = client
	      .request(context.request().method(), path, response -> {
	    	   logger.info("chemin"+path);
	        response.bodyHandler(body -> {
	          if (response.statusCode() >= 500) { // api endpoint server error, circuit breaker should fail
	            cbFuture.fail(response.statusCode() + ": " + body.toString());
	          } else {
	            HttpServerResponse toRsp = context.response()
	              .setStatusCode(response.statusCode());
	            response.headers().forEach(header -> {
	              toRsp.putHeader(header.getKey(), header.getValue());
	            });
	            // send response
	            toRsp.end(body);
	            cbFuture.complete();
	          }
	          ServiceDiscovery.releaseServiceObject(discovery, client);
	        });
	      });
	    // set headers
	    context.request().headers().forEach(header -> {
	      toReq.putHeader(header.getKey(), header.getValue());
	    });
	    if (context.user() != null) {
	      toReq.putHeader("user-principal", context.user().principal().encode());
	    }
	    // send request
	    if (context.getBody() == null) {
	      toReq.end();
	    } else {
	      toReq.end(context.getBody());
	    }
	  }

}
