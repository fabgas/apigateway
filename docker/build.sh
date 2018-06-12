docker build -t "microservice/apigateway" $DIR/../apigateway
docker build -t "microservice/order-microservice" $DIR/../order
docker build -t "microservice/customer-microservice" $DIR/../customer

docker-compose.