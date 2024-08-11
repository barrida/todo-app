# Working With Authorization Server

Auth server is taken from Spring Security [sample](https://github.com/spring-projects/spring-security-samples/tree/main/servlet/spring-boot/java/oauth2/authorization-server) and enhanced by Suleyman Yildirim. You'll need to get a JWT token from the Authorization Server in order to call HTTP Endpoints.

### Step 1: Build auth-server image

```shell
docker build -t authorization-server-image .
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

Now that you have the access token, you can use it to send a http request. HTTP POST to Register a User to the `/v1/register-user` endpoint:

```shell
curl -X POST http://localhost:8080/v1/register-user \
    -H "Authorization: Bearer <your_access_token>" \
    -H "Content-Type: application/json" \
    -d '{
          "userId": 0,
          "username": "newuser",
          "email": "newuser@example.com",
          "password": "password123"
        }'
```
