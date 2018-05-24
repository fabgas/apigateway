package org.alma.apigateway;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
/**
 * Définition d'une api gateway.
 * Capture tous les appel REST et les redispatch vers les bons services
 * @author Fabrice
 *
 */
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
public class OrderRestVerticle extends AbstractVerticle {
	/**
	 * Définition du discovery de service
	 */
	protected ServiceDiscovery discovery;
	/**
	 * Définition du Logger
	 */
	  private static final Logger logger = LoggerFactory.getLogger(OrderRestVerticle.class);
	@Override
	public void start(Future<Void> startFuture) throws Exception {
		
		discovery = ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions().setBackendConfiguration(config()));
//		super.start(startFuture);
		logger.info("Démarrage de l'API gateway");
		// définition du router
	    Router router = Router.router(vertx); 
	    // body handler
	    router.route().handler(BodyHandler.create());
	    // api dispatcher
	    router.route("/api/order/*").handler(this::dispatchRequests);
	    
	    vertx
        .createHttpServer()
        .requestHandler(router::accept)
        .listen(7001, result -> {
          if (result.succeeded()) {
        	  logger.info(" Appel on order api succeed");
        	  startFuture.complete();
          } else {
        	  startFuture.fail(result.cause());
          }
        });
	}
	private void dispatchRequests(RoutingContext context) {
		logger.info("Appel de  gateway");
		 HttpServerResponse response = context.response();
		   response
		       .putHeader("content-type", "text/html")
		       .end("<h1>Hello from my first Vert.x 3 application</h1>");	  }

}
