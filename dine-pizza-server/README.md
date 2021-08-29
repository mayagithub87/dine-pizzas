# Dine Pizza Server

### Requisites
* VSCode or intelliJ Idea IDE
* Maven installed

### Build and Run Project

Execute the following command for putting up and running server project. The inventory csv is expected as an argument.

`./mvnw spring-boot:run -Dspring-boot.run.arguments=--inventory=D:/inventory.csv`


### Configuration

At resources folder, the file application.properties may be configured for updanting server context path and oven's backing time in seconds.

`
server.servlet.contextPath=/dine-pizza-server
baking-time=30
`

### API Documentation

API was documented using Swagger library. At the following url you may see the information after project is running:

`http://localhost:8080/dine-pizza-server/swagger-ui/#/orders-controller/`