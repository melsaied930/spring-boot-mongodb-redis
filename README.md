# Spring Boot MongoDB + Redis Demo

This is a modern, production-style demo application built with Spring Boot 3.4.5, MongoDB, and Redis. It demonstrates
RESTful CRUD operations on `User` entities, supports Redis caching, and includes transaction-level logging for both
MongoDB and Redis.

---

## 🚀 Features

- Spring Boot 3.4.5 with Java 21
- MongoDB document storage
- Redis caching with toggleable support
- AOP logging of MongoDB and Redis operations
- Cache metrics reporting and management
- Docker Compose integration for MongoDB, Redis, Mongo Express
- REST API for user management

---

## 📁 Project Structure (Key)

```
src/main/java/com/example/spring_boot_mongodb_redis/
├── controller/
│   ├── UserController.java
│   └── CacheController.java
├── service/
│   ├── UserService.java
│   └── CacheMetrics.java
├── model/
│   ├── User.java
│   └── DatabaseSequence.java
├── config/
│   ├── RedisCacheConfig.java
│   ├── NoOpCacheConfig.java
│   ├── JacksonConfig.java
│   └── SequenceGeneratorService.java
├── aspect/
│   └── TransactionLoggingAspect.java
├── interceptor/
│   └── MetricsRecordingCacheInterceptor.java
├── init/
│   └── UserDataInitializer.java
└── repository/
    └── UserRepository.java
```

---

## 🔧 Configuration

Toggle Redis caching:

```yaml
cache:
  enabled: true  # or false
```

Set logging levels:

```yaml
logging:
  level:
    MongoDBTransactions: DEBUG
    RedisTransactions: DEBUG
    ServiceOperations: INFO
```

---

## 🔌 REST API Overview

| Method | Endpoint             | Description              |
|--------|----------------------|--------------------------|
| GET    | `/api/users`         | List all users           |
| GET    | `/api/users/{id}`    | Get user by ID           |
| POST   | `/api/users`         | Create a new user        |
| PUT    | `/api/users/{id}`    | Update an existing user  |
| DELETE | `/api/users/{id}`    | Delete user by ID        |
| GET    | `/api/cache/metrics` | View Redis cache metrics |
| GET    | `/api/cache/status`  | View cache status info   |
| POST   | `/api/cache/clear`   | Clear all caches         |

---

## 📊 Logs

MongoDB and Redis transactions are logged using AOP and categorized:

- `MongoDBTransactions`
- `RedisTransactions`
- `ServiceOperations`

Example MongoDB log:

```
MongoDB Transaction Start: UserRepository.findById with args: [1]
MongoDB Transaction End: UserRepository.findById - execution time: 15 ms
```

Example Redis log:

```
Redis Cache HIT for cache: users
Redis Cache Operation End: UserService.getById - execution time: 2 ms
```

---

## 🐳 Run with Docker

Start containers:

```bash
docker compose up -d
```

Stop containers:

```bash
docker compose down -v
```

Ports:

- MongoDB: `27017`
- Redis: `6379`
- Mongo Express: `8081`

---

## ▶️ Local Development

```bash
./mvnw clean spring-boot:run
```

---

## 🔄 Load Initial Data

Place your `user.json` in:

```
src/main/resources/user.json
```

---

## ✅ Tech Stack

- Java 21
- Spring Boot 3.4.5
- MongoDB + Spring Data MongoDB
- Redis + Spring Cache
- AOP + Caching Metrics
- Docker Compose

---

> Maintained by Mohamed Elsayed