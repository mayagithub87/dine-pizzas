# Dine Pizza Server

### Requisites
* VSCode or intelliJ Idea IDE
* Maven installed

### Build and Run Project

Execute the following command for putting up and running server project. The inventory csv is expected as an argument.

`./mvnw spring-boot:run`

### Configuration

At resources folder, the file application.properties may be configured for updanting server context path and oven's backing time in seconds.

Web server configuration, do not change this if not needed.
`server.servlet.contextPath=/dine-pizza-server`
`server.port=8081`
Every 5 seconds two crons are in charge to check and process pizza orders.
`order-cron-delay=5`

Baking time in seconds.
`baking-time=30`
Ovens amount.
`ovens-count=2`

These two last variables may be configured at server startup.

### Toppings inventory
At server startup the inventory.csv path is requested. Please provide it for correct server work.

### API Documentation

API was documented using Swagger library. At the following url you may see the information after project is running:

`http://localhost:8081/dine-pizza-server/swagger-ui/#/orders-controller/`