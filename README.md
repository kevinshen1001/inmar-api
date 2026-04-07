# INMAR Metadata API

A RESTful API for managing hierarchical product metadata (Location → Department → Category → Subcategory) with SKU search, built with **Spring Boot 3**, **Kotlin**, **PostgreSQL**, and **Docker**.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 1.9 |
| Framework | Spring Boot 3.2 |
| Persistence | Spring Data JPA + PostgreSQL |
| Migrations | Flyway |
| Security | Spring Security (Basic Auth + JWT) |
| Logging | Logback + Logstash JSON encoder + Trace IDs |
| Testing | JUnit 5, Testcontainers, REST Assured, Mockito-Kotlin |
| Containerisation | Docker + Docker Compose |

---

## Quick Start

```bash
# Clone / unzip the project, then:
./start.sh            # Build images, start all containers, wait for health

./start.sh --build    # Force Docker image rebuild
./start.sh --logs     # Tail API logs
./start.sh --down     # Stop everything and remove volumes
```

The script will print the API URL, example curl commands, and credentials when the stack is ready.

---

## Default Credentials

| User | Password | Role |
|---|---|---|
| `admin` | `password` | ADMIN – full CRUD |
| `user` | `password` | USER – read + SKU search |

---

## Authentication

### JWT (recommended for UI / sessions)

```bash
# 1. Obtain token
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"password"}' | jq -r '.data.token')

# 2. Use in subsequent requests
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/v1/location
```

### Basic Auth (recommended for API-to-API)

```bash
curl -u admin:password http://localhost:8080/api/v1/location
```

---

## API Endpoints

All endpoints are prefixed with `/api/v1`.

### Auth
| Method | Path | Description |
|---|---|---|
| POST | `/auth/login` | Get JWT token |

### Location
| Method | Path | Auth |
|---|---|---|
| GET | `/location` | USER |
| GET | `/location/{id}` | USER |
| POST | `/location` | ADMIN |
| PUT | `/location/{id}` | ADMIN |
| DELETE | `/location/{id}` | ADMIN |

### Department
| Method | Path | Auth |
|---|---|---|
| GET | `/location/{locationId}/department` | USER |
| GET | `/location/{locationId}/department/{departmentId}` | USER |
| POST | `/location/{locationId}/department` | ADMIN |
| PUT | `/location/{locationId}/department/{departmentId}` | ADMIN |
| DELETE | `/location/{locationId}/department/{departmentId}` | ADMIN |

### Category & Subcategory
Same pattern, nested under department and category respectively.

### SKU Search
| Method | Path | Body / Params |
|---|---|---|
| POST | `/sku/search` | JSON body with `location`, `department`, `category`, `subcategory` |
| GET | `/sku/search` | Query params: `?location=X&department=Y&category=Z&subcategory=W` |

All fields are optional and matching is **case-insensitive**.

#### Example – find SKUs 1 & 14

```bash
curl -u user:password -X POST http://localhost:8080/api/v1/sku/search \
  -H 'Content-Type: application/json' \
  -d '{
    "location": "Perimeter",
    "department": "Bakery",
    "category": "Bakery Bread",
    "subcategory": "Bagels"
  }'
```

---

### Example - Search Departments

```bash
# Search by name
curl -u user:password "http://localhost:8080/api/v1/department/search?q=Meat"
curl -u user:password "http://localhost:8080/api/v1/department/search?q=gRoCery"
curl -u user:password "http://localhost:8080/api/v1/department/search?q=dry"

# Search by description
curl -u user:password "http://localhost:8080/api/v1/department/search?q=ice"
curl -u user:password "http://localhost:8080/api/v1/department/search?q=fresh"

```

## Logging & Trace IDs

Every request is assigned a unique `traceId` (UUID) injected into:
- MDC (visible in all log lines as `traceId=…`)
- Response header `X-Trace-Id`
- Error response body `traceId` field

You can supply your own trace ID by sending `X-Trace-Id: <your-id>` in the request.

Log files (JSON format) are written to the `api_logs` Docker volume, mountable at `/app/logs`.

---

## Running Tests

```bash
# Unit tests only (no DB required)
./gradlew test --tests "com.inmar.metadata.service.*"

# All tests (requires Docker for Testcontainers)
./gradlew test

# Test report
open build/reports/tests/test/index.html
```

---

## Environment Variables

| Variable | Default | Description |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/inmar` | JDBC URL |
| `DB_USERNAME` | `inmar` | DB username |
| `DB_PASSWORD` | `inmar` | DB password |
| `SERVER_PORT` | `8080` | API port |
| `JWT_SECRET` | *(set in compose)* | JWT signing key (min 32 chars) |
| `ADMIN_USERNAME` | `admin` | Default admin username |
| `ADMIN_PASSWORD` | `password` | Default admin password |
| `USER_USERNAME` | `user` | Default user username |
| `USER_PASSWORD` | `password` | Default user password |
