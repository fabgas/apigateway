package org.alma.apigateway;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Verticle qui sert de Gateway
 * @author Fabrice
 *
 */
public class GatewayAsVerticle extends AbstractVerticle {
	private static final Logger logger = LoggerFactory.getLogger(GatewayAsVerticle.class);

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		super.start(startFuture);
		logger.info("Démarrage du verticle gateway");
		vertx.deployVerticle(ApiGatewayVerticle.class.getName(),   new DeploymentOptions().setConfig(config()));
		logger.info("Fin du déploiement");
		
	}

}
