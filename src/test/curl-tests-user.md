Here are the corresponding `curl` commands for the endpoints defined in your `UserController` class:

---

### ✅ `GET /api/users` — List all users

```bash

curl -X GET http://localhost:8080/api/users
```

---

### ✅ `POST /api/users` — Create a new user

```bash

curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "maidenName": "Smith",
    "gender": "male",
    "email": "john.doe@example.com",
    "phone": "+1234567890",
    "username": "johndoe",
    "password": "password123",
    "birthDate": "1990-01-01"
  }'
```

---

### ✅ `GET /api/users/{id}` — Get a user by ID

```bash

curl -X GET http://localhost:8080/api/users/101
```

---

### ✅ `PUT /api/users/{id}` — Update a user

```bash

curl -X PUT http://localhost:8080/api/users/101 \
  -H "Content-Type: application/json" \
  -d '{
    "id": "101",
    "firstName": "John",
    "lastName": "Doe Updated",
    "maidenName": "Smith",
    "gender": "male",
    "email": "john.updated@example.com",
    "phone": "+1234567890",
    "username": "johnny",
    "password": "newpassword",
    "birthDate": "1990-01-01"
  }'
```

> ⚠️ Note: Even though you're updating, you still include the full user object (with `id` if needed, depending on how
> your service handles updates).

---

### ✅ `DELETE /api/users/{id}` — Delete a user

```bash

curl -X DELETE http://localhost:8080/api/users/101
```

---

Let me know if you want to:

* test UUID-based IDs (update controller signature),
* send a batch of users with one call,
* or include authentication headers (e.g., Bearer token).
