### Anton HttpServer
#### Features

- Support GET, POST, PUT
- Static resource

#### How to use:
```java
HttpServer httpServer = new HttpServer(new HttpServerConfig());

httpServer.addReaction("/", RequestMethods.GET, ((request, response) -> {
    System.out.println("Hello http");
}));

httpServer.deploy();
```