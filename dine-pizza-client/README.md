# Dine Pizza Client

### Requisites
* JDK 1.8 minimum
* Maven installed
* VSCode or intelliJ Idea IDE

### Build and Run Project

Execute the following command for putting up and running client project.

`./mvnw spring-boot:run`

### Configuration

At resources folder, the file application.properties may be configured for updanting server api url and websocket for listening server notifications.

`
dine-pizza-api-url=http://localhost:8081/dine-pizza-server
dine-pizza-websocket-url=ws://localhost:8081/dine-pizza-server/dine-pizza-websocket
`

### Generate Executable Jar

Following line generate a standalone executable client, that may be used typing java -jar 'jar file'.

`./mvnw clean package spring-boot:repackage`