FROM java:8-jre

ENV VERTICLE_FILE target/customer-fat.jar

# Set the location of the verticles
ENV VERTICLE_HOME /opt/verticles

EXPOSE 7000

COPY $VERTICLE_FILE $VERTICLE_HOME/
COPY config/docker.json $VERTICLE_HOME/


WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["java -Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.SLF4JLogDelegateFactory -jar customer-fat.jar -cluster -ha -conf docker.json"]
