# apigateway
building an api gateway with vert.x

A project name apigateway will route the REST call to the right REST API : order or user based on path /api/user ou /api/order.

Steps :
 1 / develop two verticles listening on port 7000 (user) et 7001 (order)  TODO
 2 / set them in a cluster TODO 
 3 / add an api gateway : service discovery with httpendpoint TODO
 4 / communication between services via an rest call
 3 / add an api gateway : service discovery with event bus TODO
 5/ communication between services via an avent bus. TODO

