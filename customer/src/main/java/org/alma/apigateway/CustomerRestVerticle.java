package org.alma.apigateway;

import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
public class CustomerRestVerticle extends RestApiVertical {
	  private static final String SERVICE_NAME = "customer-api";/**
	 * Définition du Logger
	 */
	  private static final Logger logger = LoggerFactory.getLogger(CustomerRestVerticle.class);
	@Override
	public void start(Future<Void> startFuture) throws Exception {
		
		super.start(startFuture);
		logger.info("Démarrage de l'API gateway");
		// définition du router
	    Router router = Router.router(vertx); 
	    // body handler
	    router.route().handler(BodyHandler.create());
	    // api dispatcher
	    router.route("/api/customer/*").handler(this::dispatchRequests);

	    // get HTTP host and port from configuration, or use default value
	    String host = config().getString("customer.http.address", "0.0.0.0");
	    int port = config().getInteger("customer.http.port", 8086);
	    String apiname = config().getString("api.name");
	    logger.info("customer apiname:" + apiname+" host :" + host + ", port :"+port);
	    logger.info("config "+config());
	    createHttpServer(router, host, port)
	      .compose(serverCreated -> publishHttpEndpoint(SERVICE_NAME, host, port))
	      .setHandler(startFuture.completer());
	}
	private void dispatchRequests(RoutingContext context) {
		logger.info("Appel de  gateway");
		 HttpServerResponse response = context.response();
		   response
		       .putHeader("content-type", "text/html")
		       .end("<h1>Hello from customer microservice</h1>");	  }

}
