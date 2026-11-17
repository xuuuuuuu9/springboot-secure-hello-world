# Secure Hello World API

A Spring Boot 3.2.0 take-home project with a Spring Security-protected Hello World endpoint and a username/password login API.

## Requirements

- Java 17 or newer
- Maven 3.6.3 or newer

## Run

```bash
mvn test
mvn spring-boot:run
```

The server starts at `http://localhost:8080` by default.

## API

| Method | Path | Result |
| --- | --- | --- |
| `POST` | `/api/login` | Authenticates JSON credentials and creates a session |
| `GET` | `/api/hello` | Requires the session; returns the exact text `Hello World` |

Demo credentials: username `test`, password `123456`.

```bash
# Anonymous request: 401
curl -i http://localhost:8080/api/hello

# Log in; keep the JSESSIONID cookie
curl -i -c cookies.txt -H 'Content-Type: application/json' \
  -d '{"username":"test","password":"123456"}' \
  http://localhost:8080/api/login

# Authenticated request: 200, Hello World
curl -i -b cookies.txt http://localhost:8080/api/hello

# Incorrect password: 401
curl -i -H 'Content-Type: application/json' \
  -d '{"username":"test","password":"wrong"}' \
  http://localhost:8080/api/login
```

This exercise uses an in-memory account and server-side sessions. CSRF protection is disabled for the demo JSON API; a browser-facing production app should add an appropriate CSRF strategy. Do not commit `cookies.txt`.
