# Installing, Configuring, and Running Spring Boot with MongoDB on Ubuntu VM

Here's a comprehensive guide to setting up a Spring Boot application with MongoDB on an Ubuntu server.

## Prerequisites
- Ubuntu server (20.04 or 22.04 LTS recommended)
- SSH access to the server
- Basic knowledge of Linux commands
- Java JDK installed (we'll cover this)
- Maven or Gradle installed (we'll cover this)

## Part 1: Setting Up the Ubuntu Server

### 1. Update System Packages
```bash

sudo apt update && sudo apt upgrade -y
```

### 2. Install Java JDK
Spring Boot requires Java 17 or later (as of Spring Boot 3.x):

```bash

sudo apt install openjdk-17-jdk -y
```

Verify installation:
```bash

java -version
```

### 3. Install Maven (for building Spring Boot)
```bash

sudo apt install maven -y
```

Verify installation:
```bash

mvn -version
```

## Part 2: Installing MongoDB

### 1. Import MongoDB GPG Key
```bash

sudo apt-get install gnupg
wget -qO - https://www.mongodb.org/static/pgp/server-6.0.asc | sudo apt-key add -
```

### 2. Create MongoDB List File
```bash

echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu $(lsb_release -cs)/mongodb-org/6.0 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-6.0.list
```

### 3. Update Package Database
```bash

sudo apt-get update
```

### 4. Install MongoDB
```bash

sudo apt-get install -y mongodb-org
```

### 5. Start MongoDB Service
```bash

sudo systemctl start mongod
sudo systemctl enable mongod
```

### 6. Verify MongoDB is Running
```bash

sudo systemctl status mongod
```

### 7. (Optional) Secure MongoDB
By default, MongoDB has no authentication. To add basic security:

```bash

mongosh
```

Then in the MongoDB shell:
```javascript
use admin
db.createUser({
  user: "admin",
  pwd: "yourStrongPassword",
  roles: [ { role: "userAdminAnyDatabase", db: "admin" } ]
})
exit
```

Edit MongoDB config:
```bash

sudo nano /etc/mongod.conf
```

Add/modify these lines:
```yaml
security:
  authorization: enabled
```

Restart MongoDB:
```bash

sudo systemctl restart mongod
```

## Part 3: Setting Up Spring Boot Application

### Option A: Deploying an Existing Spring Boot App

1. Upload your Spring Boot JAR file to the server (using SCP or SFTP)

   Example with SCP:
   ```bash

   scp target/your-application.jar user@your-server-ip:/home/username/
   ```

2. Run the application:
   ```bash

   java -jar your-application.jar
   ```

### Option B: Creating a New Spring Boot App on the Server

1. Install Git (if needed):
   ```bash

   sudo apt install git -y
   ```

2. Clone your Spring Boot project or initialize a new one:
   ```bash

   git clone https://github.com/your-repo/spring-boot-mongo.git
   cd spring-boot-mongo
   ```

3. Build the project:
   ```bash

   mvn clean package
   ```

4. Run the application:
   ```bash

   java -jar target/your-application.jar
   ```

## Part 4: Configuring Spring Boot to Connect to MongoDB

### 1. Typical Spring Boot MongoDB Configuration

In your `application.properties` or `application.yml`:

For unauthenticated MongoDB (not recommended for production):
```properties
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=yourdbname
```

For authenticated MongoDB:
```properties
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=yourdbname
spring.data.mongodb.username=admin
spring.data.mongodb.password=yourStrongPassword
spring.data.mongodb.authentication-database=admin
```

### 2. Sample Spring Boot MongoDB Dependency

Ensure your `pom.xml` includes:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

## Part 5: Running as a Service (Production)

### 1. Create a systemd service file
```bash

sudo nano /etc/systemd/system/springboot.service
```

Add this content (adjust paths as needed):
```ini
[Unit]
Description=Spring Boot Application
After=syslog.target network.target mongod.service

[Service]
User=yourusername
WorkingDirectory=/home/yourusername/your-application-folder
ExecStart=/usr/bin/java -jar /home/yourusername/your-application-folder/target/your-application.jar
SuccessExitStatus=143

[Install]
WantedBy=multi-user.target
```

### 2. Enable and start the service
```bash

sudo systemctl daemon-reload
sudo systemctl enable springboot
sudo systemctl start springboot
```

### 3. Check status
```bash

sudo systemctl status springboot
```

## Part 6: Connecting to the Application

### 1. Ensure firewall allows traffic
```bash

sudo ufw allow 8080  # Default Spring Boot port
sudo ufw enable
```

### 2. Access the application
- If running on port 8080: `http://your-server-ip:8080`
- If you configured a different port, use that instead

## Troubleshooting

1. **Connection refused errors**:
    - Check MongoDB is running: `sudo systemctl status mongod`
    - Check MongoDB logs: `sudo journalctl -u mongod -n 50 --no-pager`

2. **Authentication errors**:
    - Verify MongoDB user credentials
    - Check if authentication is enabled in mongod.conf

3. **Spring Boot not starting**:
    - Check logs: `sudo journalctl -u springboot -n 50 --no-pager`
    - Run manually to see errors: `java -jar your-application.jar`

4. **Port already in use**:
    - Find and kill the process: `sudo lsof -i :8080` then `sudo kill -9 PID`

This comprehensive guide should get your Spring Boot application with MongoDB up and running on your Ubuntu VM server.