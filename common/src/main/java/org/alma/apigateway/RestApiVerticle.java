package org.alma.apigateway;

import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

public class RestApiVerticle extends DiscoveryAbstractVerticle {
	  /**
	   * Create http server for the REST service.
	   *
	   * @param router router instance
	   * @param host   http host
	   * @param port   http port
	   * @return async result of the procedure
	   */
	  protected Future<Void> createHttpServer(Router router, String host, int port) {
	    Future<HttpServer> httpServerFuture = Future.future();
	    vertx.createHttpServer()
	      .requestHandler(router::accept)
	      .listen(port, host, httpServerFuture.completer());
	    return httpServerFuture.map(r -> null);
	  }
}
