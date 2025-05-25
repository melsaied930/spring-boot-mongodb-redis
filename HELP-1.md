Here’s a clean and concise summary you can add to your `HELP.md` to document the MongoDB server fix steps:

---

## 🧰 MongoDB Server Recovery Guide

This section outlines the steps followed to recover and reinitialize a MongoDB server with a replica set configuration.

### ✅ Steps to Fix MongoDB

1. **Check MongoDB Data Path**

   ```bash
   cat /etc/mongod.conf | grep dbPath
   ```

2. **Stop MongoDB Service**

   ```bash
   sudo systemctl stop mongod
   ```

3. **Delete Corrupted Local/WiredTiger Files**

   ```bash
   sudo rm -rf /var/lib/mongodb/local /var/lib/mongodb/local.*
   sudo rm -rf /var/lib/mongodb/WiredTiger* /var/lib/mongodb/storage.bson
   ```

4. **Restart MongoDB Service**

   ```bash
   sudo systemctl start mongod
   ```

5. **Verify MongoDB is Running**

   ```bash
   sudo systemctl status mongod
   ```

6. **Connect to MongoDB**

   ```bash
   mongosh --host <SERVER_IP> --port 27017
   ```

7. **Initialize Replica Set**

   ```js
   rs.initiate({
     _id: "rs0",
     members: [{ _id: 0, host: "<SERVER_IP>:27017" }]
   })
   ```

8. **(If already initialized) Force Reconfiguration**

   ```js
   rs.reconfig({
     _id: "rs0",
     members: [{ _id: 0, host: "<SERVER_IP>:27017" }]
   }, { force: true })
   ```

9. **Confirm Replica Set Status**

   ```js
   rs.status()
   ```

10. **Verify Databases**

```js
show dbs
```

> ℹ️ **Note**: Replace `<SERVER_IP>` with the actual IP (e.g., `127.0.0.1`).

---

Let me know if you want this written in Arabic as well or included in a specific section of your existing documentation.
