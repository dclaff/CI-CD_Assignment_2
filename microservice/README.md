# Microservice (Spring Boot + SQLite)

Quick start

Build and run locally with Maven:

```bash
mvn package
java -jar target/microservice-0.0.1-SNAPSHOT.jar
```

Run with Docker Compose:

```bash
docker compose up --build
```

Endpoints

- `GET /items` - list items
- `GET /items/{id}` - get item
- `POST /items` - create item (JSON body)
- `PUT /items/{id}` - update item
- `DELETE /items/{id}` - delete item
