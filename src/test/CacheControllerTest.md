Here are the `curl` commands for each of the API endpoints in your `CacheController`:

---

### ✅ 1. `GET /api/cache/metrics`

Returns cache metrics and implementation details.

```sh

curl -X GET http://localhost:8080/api/cache/metrics
```

---

### ✅ 2. `GET /api/cache/status`

Returns the current cache status including implementation and cache names.

```sh

curl -X GET http://localhost:8080/api/cache/status
```

---

### ✅ 3. `POST /api/cache/clear`

Clears all caches and resets metrics.

```sh

curl -X POST http://localhost:8080/api/cache/clear
```

---

Let me know if you want a Bash script (`.sh`) to run them all sequentially, or sample JSON responses for each.
