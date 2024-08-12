# Todo-App with Couchbase Setup
This document provides instructions to build and run the todo-app which depends on a Couchbase database and Spring Security Authorization Server, using Docker. The setup includes configuring the todo-app to communicate with Couchbase and Authorization Server via Docker containers.

## Prerequisites
Ensure you have the following installed:

- Docker
- Docker Compose (if you want to use it later)
- Couchbase
- Authorization server

## Run couchbase

Pull couchbase image:

```shell
docker pull couchbase
```

Build image:  

```shell
docker build -t couchbase-custom ./couchbase
```

Create a custom Docker network. This ensures both containers can communicate:

```shell
docker network create todo-network
```

Run the Couchbase Container:

```shell
docker run -d -p 8091-8093:8091-8093 \
-e COUCHBASE_ADMINISTRATOR_USERNAME=Administrator \
-e COUCHBASE_ADMINISTRATOR_PASSWORD=password \
-e COUCHBASE_BUCKET=default \
-e COUCHBASE_BUCKET_PASSWORD= \
--network="todo-network" \
--name couchbase1 couchbase-custom 
```

Make sure couchbase container is up and running. Check if the server and default bucket is created here http://localhost:8091/ui/index.html

## Run todo-app

Build image:

```shell
docker build -t todo-app .
```

Run the todo-app Container. Pass the COUCHBASE_HOST as an environment variable:  

```shell
docker run -d -p 8080:8080 \
-e COUCHBASE_HOST=couchbase1 \
--network todo-network \
--name todo-app todo-app
```

To check if your container is running, use:

```shell
docker ps -a
``` 

If you need to access the running container's shell, you can use:

```shell
docker exec -it your-container-name /bin/bash
```

## Working With Authorization Server
Auth server is taken from Spring Security [sample](https://github.com/spring-projects/spring-security-samples/tree/main/servlet/spring-boot/java/oauth2/authorization-server) and enhanced by Suleyman Yildirim. You'll need to get a JWT token from the Authorization Server in order to call HTTP Endpoints.

### Step 1: Build auth-server image

```shell
docker build -t authorization-server-image ./authorization-server
```

### Step 2: Run authorization server container

Make sure you use same "todo-network" and name container as "authorization-server".

```shell
docker run -p 9000:9000 \
-e JWK_URI=http://authorization-server:9000/oauth2/jwks \
--network todo-network \
--name authorization-server authorization-server-image
```

### Step 3: Get a JWT token from the Authorization Server in order to call HTTP Endpoints.

Use `scope=message:read` for GET requests

```shell
curl -X POST messaging-client:secret@localhost:9000/oauth2/token -d "grant_type=client_credentials" -d "scope=message:read"
```
Use `scope=message:write` for others such as POST, PUT etc..

```shell
curl -X POST messaging-client:secret@localhost:9000/oauth2/token -d "grant_type=client_credentials" -d "scope=message:write"
```

Authorization server should return a JSON response with an access token:

```json
{
"access_token": "eyJraWQiOiIxNDY0NmYzYi0wZGU1LTQ1MDYtYTdjZi0zNWYxYWU0ZjU5MjIiLCJhbGciOiJSUzI1NiJ9...",
"token_type": "Bearer",
"expires_in": 299,
"scope": "message:write"
}
```

### Step 4: Send an HTTP Request

Now that you have the access token, you can use it to send a http request. HTTP POST to Register a User to the `/v1/users` endpoint:

```shell
curl -X POST http://localhost:8080/v1/users \
    -H "Authorization: Bearer <your_access_token>" \
    -H "Content-Type: application/json" \
    -d '{
          "userId": 0,
          "username": "newuser",
          "email": "newuser@example.com",
          "password": "password123"
        }'
```

# OpenAPI Swagger endpoint definition

Browse to Swagger to analyse endpoints http://localhost:8080/swagger-ui/index.html#/
