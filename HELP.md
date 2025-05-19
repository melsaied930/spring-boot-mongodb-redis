# Help Guide – Spring Boot MongoDB & Redis Demo

This guide provides help on running, configuring, and using the application in development and test environments.

---

## 🔧 Configuration Overview

All settings can be adjusted in `application.yaml`.

### Enable or Disable Redis Caching

```yaml
cache:
  enabled: true  # Set too false to disable Redis caching (NoOpCacheManager will be used)
```

---

## 🛠 Environment Setup

### Prerequisites

- Java 21
- Maven
- Docker & Docker Compose

---

## ▶️ Running the App

### Locally via Maven

```bash
./mvnw clean spring-boot:run
```

### With Docker Compose

```bash
docker compose up -d
```

Services:

- MongoDB → `localhost:27017`
- Redis → `localhost:6379`
- Mongo Express → `http://localhost:8081`

---

## 🧪 API Testing

### User Endpoints

- `GET /api/users` – List users
- `GET /api/users/{id}` – Get user
- `POST /api/users` – Create user
- `PUT /api/users/{id}` – Update user
- `DELETE /api/users/{id}` – Delete user

### Cache Endpoints

- `GET /api/cache/metrics` – View cache metrics
- `GET /api/cache/status` – View cache config
- `POST /api/cache/clear` – Clear caches and reset metrics

---

## 🔄 Data Initialization

The app will autoload data from `src/main/resources/user.json` at startup **if the MongoDB collection is empty**.

### Sample `user.json`

```json
[
  {
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com"
  }
]
```

---

## 🐞 Logs and Debugging

Enable detailed logs in `application.yaml`:

```yaml
logging:
  level:
    MongoDBTransactions: DEBUG
    RedisTransactions: DEBUG
    ServiceOperations: INFO
```

---

## 📤 Build & Package

```bash
./mvnw clean package
```

Then run:

```bash
java -jar target/spring-boot-mongodb-redis-0.0.1-SNAPSHOT.jar
```

---

## 🙋 FAQ

**Q: How do I disable Redis but keep MongoDB?**  
A: Set `cache.enabled=false` in `application.yaml`.

**Q: Can I test Redis performance impact?**  
A: Yes. Compare logs and timings with `cache.enabled` toggled.

---

For any issues or enhancements, contact: **Mohamed Elsayed**