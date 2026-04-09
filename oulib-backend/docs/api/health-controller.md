# HealthController API Documentation

Base path: `/api/v1/health`

Standard response wrapper used by all APIs:

```json
{
  "code": 200,
  "message": "Success",
  "result": {}
}
```

---

## GET /api/v1/health

### 1. Title
- v1 Service Health Check

### 2. Endpoint
- `/api/v1/health`

### 3. Method
- `GET`

### 4. URL Parameters
- `None`

### 5. Message Payload
- `None`

### 6. Header Parameters
- `Content-Type: application/json`
- `Authorization: None`

### 7. Authentication & Authorization Summary
- Authentication: `Public`
- Authorization: `No role restriction`

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Health check successful.

#### ❌ Error Responses
- `500 Internal Server Error`
  - Internal code: `1000`
  - Message: `Uncategorized Error`
  - Condition: Any unhandled runtime exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X GET "http://localhost:8080/api/v1/health"
```

#### Request JSON
```json
None
```

#### Success Response JSON
```json
{
  "code": 200,
  "message": "Success",
  "result": "Service is healthy"
}
```

#### Error Response JSON
```json
{
  "code": 1000,
  "message": "Uncategorized Error",
  "result": null
}
```

---

## GET /api/v1/health/rabbitmq

### 1. Title
- v1 RabbitMQ Connection Check

### 2. Endpoint
- `/api/v1/health/rabbitmq`

### 3. Method
- `GET`

### 4. URL Parameters
- `None`

### 5. Message Payload
- `None`

### 6. Header Parameters
- `Content-Type: application/json`
- `Authorization: None`

### 7. Authentication & Authorization Summary
- Authentication: `Public`
- Authorization: `No role restriction`

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Test message published to RabbitMQ.

#### ❌ Error Responses
- `500 Internal Server Error`
  - Internal code: `1000`
  - Message: `Uncategorized Error`
  - Condition: RabbitMQ publisher failure or any unhandled runtime exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X GET "http://localhost:8080/api/v1/health/rabbitmq"
```

#### Request JSON
```json
None
```

#### Success Response JSON
```json
{
  "code": 200,
  "message": "Success",
  "result": "RabbitMQ connection test message sent"
}
```

#### Error Response JSON
```json
{
  "code": 1000,
  "message": "Uncategorized Error",
  "result": null
}
```
