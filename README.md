# Todo-App with Couchbase Setup
This document provides instructions to build and run the todo-app which depends on a Couchbase database, using Docker. The setup includes configuring the todo-app to communicate with Couchbase via Docker containers.

## Prerequisites
Ensure you have the following installed:

- Docker
- Docker Compose (if you want to use it later)

## Run couchbase

Pull couchbase image:

```docker pull couchbase```

Build image:  

```docker build -t couchbase-custom ./couchbase```


Create a custom Docker network. This ensures both containers can communicate:

``` 
docker network create todo-network
```

Run the Couchbase Container:

```
docker run -d -p 8091-8093:8091-8093 \
-e COUCHBASE_ADMINISTRATOR_USERNAME=Administrator \ 
-e COUCHBASE_ADMINISTRATOR_PASSWORD=password \
-e COUCHBASE_BUCKET=default \
-e COUCHBASE_BUCKET_PASSWORD= \
--network="todo-network" \
--name couchbase1 couchbase-custom 
```

## Run todo-app

Build image:

```
docker build -t todo-app .
```

Run the todo-app Container. Pass the COUCHBASE_HOST as an environment variable:  

```
docker run -d -p 8080:8080 \
-e COUCHBASE_HOST=couchbase1 \
--network todo-network \
--name todo-app todo-app
```

To check if your container is running, use:

```
docker ps -a
``` 

If you need to access the running container's shell, you can use:

```
docker exec -it your-container-name /bin/bash
```