# 🛠️ Help Guide – Spring Boot MongoDB & Redis Demo

This guide covers how to configure, run, test, and debug the Spring Boot application using **MongoDB**, **Redis**, and **Docker Compose**.

---

## 📦 Project Overview

This project demonstrates:

* REST APIs for user management
* Redis-based caching with metrics
* MongoDB persistence
* Dockerized infrastructure with health checks

---

## 🔧 Configuration

All main settings are located in `application.yaml` and `.env`.

### ✅ Cache Configuration

Enable/disable Redis caching:

```yaml
spring:
  cache:
    type: redis
    redis:
      time-to-live: 60000
      cache-null-values: false
```

Or override via `.env`:

```dotenv
SPRING_DATA_REDIS_PASSWORD=password
```

To **disable caching**, replace `type: redis` with:

```yaml
type: none
```

---

## ⚙️ Environment Setup

### 🔰 Prerequisites

* Java 21
* Maven 3.9+
* Docker + Docker Compose
* MongoDB Shell (`mongosh`)
* Redis CLI (`redis-cli`)

---

## ▶️ Running the Application

### 💻 Locally via Maven

```bash
set -a && source .env && set +a
./mvnw clean spring-boot:run
```

### 🐳 With Docker Compose

```bash
docker compose --env-file .env up -d
```

> Access services:

* MongoDB: `localhost:27017`
* Redis: `localhost:6379`
* Mongo Express UI: [http://localhost:8081](http://localhost:8081)

---

## 🧪 API Endpoints

### 👤 User API

| Method | Endpoint          | Description     |
| ------ | ----------------- | --------------- |
| GET    | `/api/users`      | List all users  |
| GET    | `/api/users/{id}` | Get user by ID  |
| POST   | `/api/users`      | Create new user |
| PUT    | `/api/users/{id}` | Update user     |
| DELETE | `/api/users/{id}` | Delete user     |

### 🧠 Cache API

| Method | Endpoint             | Description               |
| ------ | -------------------- | ------------------------- |
| GET    | `/api/cache/status`  | View cache configuration  |
| GET    | `/api/cache/metrics` | View cache hit/miss stats |
| POST   | `/api/cache/clear`   | Clear all caches          |

---

## 📥 Data Initialization

If the MongoDB collection is **empty** at startup, the app will auto-load `src/main/resources/user.json`.

### Example `user.json` format:

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

## 🐞 Debugging & Logs

Enable verbose logging in `application.yaml`:

```yaml
logging:
  level:
    MongoDBTransactions: DEBUG
    RedisTransactions: DEBUG
    ServiceOperations: INFO
```

To test Redis impact:

* Compare logs with/without caching.
* Toggle caching in `application.yaml`.

---

## 🚀 Build & Package

Build the app:

```bash
./mvnw clean package
```

Then run:

```bash
java -jar target/spring-boot-mongodb-redis-0.0.1-SNAPSHOT.jar
```

---

## 🧪 Manual Connectivity Tests

### 🔁 Ping MongoDB

```bash
mongosh \
  --host 127.0.0.1 \
  --port 27017 \
  --username root \
  --password secret \
  --authenticationDatabase admin \
  --eval "db.adminCommand({ ping: 1 })"
```

### 💡 Ping Redis

```bash
redis-cli \
  -h 127.0.0.1 \
  -p 6379 \
  -a password ping
# Expected: PONG
```

---

## 🙋 FAQ

**Q: How do I disable Redis caching only?**
A: Set `spring.cache.type=none` in `application.yaml`.

**Q: How can I verify Redis performance difference?**
A: Enable `RedisTransactions` log and compare method timings with/without caching.

**Q: Is Docker required?**
A: No, but it simplifies local infrastructure setup and isolation.

---

For support, reach out to: **Mohamed Elsayed**

---

Would you like this saved as a file or copied into your project automatically?
