# OULib Backend - Setup Guide

## Prerequisites

Before running this project, make sure you have installed:

- **Java 21** (JDK 21)
- **Maven 3.9+** (or use the included `mvnw` wrapper)
- **Docker** and **Docker Compose**
- **Git**

---

## Setup Instructions

### 1a. Clone the Repository (Skip if already done and go to step 1b )

```bash
git clone <repository-url>
cd oulib-backend
```

### 1b. Pull the newest code from main

```bash
cd oulib-backend
git checkout main
git pull origin main
git checkout -b <your_branch_name>
```

### 2. Create Environment File

Create a `.env` file in the project root with the following variables:

```env
# Database Configuration (for docker-compose.yml production)
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=oulib
MYSQL_USER=oulib
MYSQL_PASSWORD=oulib123

# Application Database Connection
DBMS_CONNECTION=jdbc:mysql://mysql:3306/oulib
DBMS_USERNAME=oulib
DBMS_PASSWORD=oulib123


# Redis Configuration
REDIS_HOST=redis
REDIS_PORT=6379


RABBITMQ_HOST=rabbitmq
RABBITMQ_USERNAME=oulib
RABBITMQ_PASSWORD=oulib

MAIL_USERNAME=your_mail@gmail.com
MAIL_PASSWORD="xxxx xxxx xxxx xxxx"

# Admin Account (initial setup)
ADMIN_EMAIL=admin@oulib.com
ADMIN_PASSWORD=12345678

# Librarian Account (initial setup)
LIBRARIAN_EMAIL=librarian@oulib.com
LIBRARIAN_PASSWORD=12345678

# JWT Signer Key (generate a secure random string, min 32 bytes, 256 bits)
SIGNER_KEY=4cc53e030260a15f819582c43fcf9f4771d0886be524767d544dbe7d51d2b7f8

# Cloudinary Configuration (for image uploads)
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```

---

## Running the Project

### Development Mode (Recommended for Local Development)

This runs only the infrastructure services (MySQL, Redis, RabbitMQ) in Docker, and you run the Spring Boot app locally.

**Start:** Start infrastructure services:
```bash
docker-compose -f docker-compose.dev.yml up -d
```
<!-- 
**Step 2:** Wait for MySQL to be healthy (check with `docker ps`), then run the application:

**Using Maven Wrapper (Windows):**
```cmd
mvnw.cmd spring-boot:run
```

**Using Maven Wrapper (Linux/Mac):**
```bash
./mvnw spring-boot:run
```

**Using installed Maven:**
```bash
mvn spring-boot:run
``` -->

The application will be available at: `http://localhost:8080`

[//]: # (---)

[//]: # ()
[//]: # (### Option B: Full Docker Mode &#40;Production-like&#41;)

[//]: # ()
[//]: # (This runs everything in Docker containers including the backend application.)

[//]: # ()
[//]: # (```bash)

[//]: # (docker-compose up -d --build)

[//]: # (```)

[//]: # ()
[//]: # (The application will be available at: `http://localhost:8080`)

[//]: # ()
[//]: # (---)

## Service Ports

| Service   | Port  | Description                    |
|-----------|-------|--------------------------------|
| Backend   | 8080  | Spring Boot API                |
| MySQL     | 3307  | Database (dev mode)            |
| MySQL     | 3306  | Database (production mode)     |
| Redis     | 6379  | Cache                          |
| RabbitMQ  | 5672  | Message Queue (AMQP)           |
| RabbitMQ  | 15672 | RabbitMQ Management UI         |

---

## Useful Commands

### Stop Services
```bash
# Development mode
docker-compose -f docker-compose.dev.yml down

# Production mode
docker-compose down
```

### View Logs
```bash
# All services
docker-compose -f docker-compose.dev.yml logs -f

# Specific service
docker-compose -f docker-compose.dev.yml logs -f mysql
```

### Rebuild Application (Production mode)
```bash
docker-compose up -d --build backend
```

### Clean Build (Local Development)
```bash
mvnw.cmd clean install -DskipTests
```

---

## Troubleshooting

### MySQL Connection Refused
- Ensure Docker containers are running: `docker ps`
- Wait for MySQL health check to pass (may take 30-60 seconds)
- Check if port 3307 (dev) or 3306 (prod) is not used by another process

### Port Already in Use
- Stop any existing MySQL/Redis services on your machine
- Or modify the port mappings in docker-compose files

### Permission Denied (Linux/Mac)
```bash
chmod +x mvnw
```

### Clear Database Data
```bash
# Stop containers first
docker-compose -f docker-compose.dev.yml down

# Remove volume data (WARNING: This deletes all data!)
rm -rf volumes/mysql
rm -rf volumes/redis
```

---

## Technology Stack

- **Java 21** + **Spring Boot 4.0.2**
- **MySQL 8.0** - Database
- **Redis** - Caching
- **RabbitMQ** - Message Queue (optional)
- **Cloudinary** - Image Storage
- **JWT** - Authentication

---

## Contact

If you have any issues, contact the project maintainer.
