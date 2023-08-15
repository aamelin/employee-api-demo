# employee-api-demo

Employee service demo

## Prerequisites

- JDK 17
- Maven
- Docker (Docker service should be started)
- Docker Compose

## API

The service allows to create, retrieve, update and delete employees.
The project uses Swagger to document the API:

- the JSON document of the API is available under `/api-docs`
- Swagger UI is available under `/swagger`

The following operations are supported:

- retrieve all employees:

Request:

```
$ curl -X GET localhost:8080/employees
```

Response:

```
[
  {
    "employeeId": "512a6753-026b-448c-857b-e79d86a9a486",
    "firstName": "Alice",
    "lastName": "Smith",
    "email": "al.sm@example.com",
    "birthday": "1987-01-01",
    "hobbies": [
      "golf"
    ]
  },
  {
    "employeeId": "bf20aecd-863e-4bd0-8867-8460e4e02cd7",
    "firstName": "Bob",
    "lastName": "Smith",
    "email": "bob.smith@example.com",
    "birthday": "1992-01-01",
    "hobbies": [
      "chess",
      "music"
    ]
  },
  {
    "employeeId": "5905c580-01d0-4c02-90a7-b2182672a7dc",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "birthday": "1977-01-01",
    "hobbies": [
      "chess",
      "music",
      "football"
    ]
  }
]
```

- retrieve an employee by their id:

Request:

```
$ curl -X GET localhost:8080/employees/
```

Response:

```
{
  "employeeId": "512a6753-026b-448c-857b-e79d86a9a486",
  "firstName": "Alice",
  "lastName": "Smith",
  "email": "al.sm@example.com",
  "birthday": "1987-01-01",
  "hobbies": [
    "golf"
  ]
}
```

- update an employee by their id with the new data:

Request:

```
$ curl -X PUT localhost:8080/employees/512a6753-026b-448c-857b-e79d86a9a486 \
        -H 'Content-Type: application/json' \
        -H 'X-API-Key: 123456' \
        -d '{"firstName": "Alice", "lastName": "Smith", "email": "al.sm@example.com", "birthday": "1987-01-01", "hobbies":["music"]}
```

Response:

```
{
  "employeeId": "512a6753-026b-448c-857b-e79d86a9a486",
  "firstName": "Alice",
  "lastName": "Smith",
  "email": "al.sm@example.com",
  "birthday": "1987-01-01",
  "hobbies": [
    "music"
  ]
}
```

- delete an employee by their id:

Request:

```
 $ curl -X DELETE localhost:8080/employees/512a6753-026b-448c-857b-e79d86a9a486 \
        -H 'Content-Type: application/json' \
        -H 'X-API-Key: 123456'
```

### Security

All modifying requests require an API key authentication to succeed, the value of the key can be set via the `app.http.api-key`, the name of the header used is defined by `app.http.api-key-header`.

## How to run

### Development mode

In development mode the service runs locally while the dependencies are managed by `docker compose` integration with Spring Boot. To start it execute the following command:

```
$ mvn clean spring-boot:run
```

### Production mode

To run the service in 'production' mode with all components running inside containers we need to first build an image for the `employee-api-demo`:

```
$ mvn clean spring-boot:build-image
```

This will create a local image `employee:0.0.1`.

Afterwards execute:

```
$ docker compose -f compose-run.yml up
```

Which will start the service together with the dependencies.

## Testing

The project includes samples of unit and integration tests aimed to verify the completenes of functionality. The tests can be run with the following commands:

- unit tests

```
$ mvn clean test
```

- integration tests

```
$ mvn clean verify
```
