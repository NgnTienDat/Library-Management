# CategoryController API Documentation

Base path: `/api/v1/categories`

Standard response wrapper used by all APIs:

```json
{
  "code": 200,
  "message": "Success",
  "result": {}
}
```

---

## POST /api/v1/categories

### 1. Title
- v1 Create Category

### 2. Endpoint
- `/api/v1/categories`

### 3. Method
- `POST`

### 4. URL Parameters
- `None`

### 5. Message Payload
Based on `CategoryCreationRequest`:

| Field | Type | Required | Description |
|---|---|---|---|
| name | string | true | Category name. `@NotBlank`. |

### 6. Header Parameters
- `Content-Type: application/json`
- `Authorization: None`

### 7. Authentication & Authorization Summary
- Authentication: `Requires JWT`
- Authorization: `LIBRARIAN` only

### 8. Response Codes

#### ✅ Success Responses
- `201 Created`: Category created.

#### ❌ Error Responses
- `400 Bad Request` / code `400`: Validation failed.
- `401 Unauthorized` / code `1003`: Missing/invalid JWT.
- `403 Forbidden` / code `1037`: Role is not `LIBRARIAN`.
- `409 Conflict` / code `1044`: Category already exists.
- `500 Internal Server Error` / code `1000`: Unhandled runtime exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X POST "http://localhost:8080/api/v1/categories" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"name":"Software Architecture"}'
```

#### Request JSON
```json
{
  "name": "Software Architecture"
}
```

#### Success Response JSON
```json
{
  "code": 201,
  "message": "Created",
  "result": {
    "id": "cat-001",
    "name": "Software Architecture"
  }
}
```

#### Error Response JSON
```json
{
  "code": 1044,
  "message": "Category already exists",
  "result": null
}
```

---

## DELETE /api/v1/categories/{id}

### 1. Title
- v1 Delete Category

### 2. Endpoint
- `/api/v1/categories/{id}`

### 3. Method
- `DELETE`

### 4. URL Parameters

| Name | Type | Required | Description |
|---|---|---|---|
| id | string | true | Category ID. |

### 5. Message Payload
- `None`

### 6. Header Parameters
- `Content-Type: application/json`
- `Authorization: Bearer <JWT_TOKEN>`

### 7. Authentication & Authorization Summary
- Authentication: `Requires JWT`
- Authorization: `LIBRARIAN` only

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Category deleted.

#### ❌ Error Responses
- `401 Unauthorized` / code `1003`: Missing/invalid JWT.
- `403 Forbidden` / code `1037`: Role is not `LIBRARIAN`.
- `404 Not Found` / code `1028`: Category not found.
- `409 Conflict` / code `1045`: Category is assigned to books.
- `500 Internal Server Error` / code `1000`: Unhandled runtime exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X DELETE "http://localhost:8080/api/v1/categories/cat-001" \
  -H "Authorization: Bearer <JWT_TOKEN>"
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
  "result": "Category deleted successfully"
}
```

#### Error Response JSON
```json
{
  "code": 1045,
  "message": "Category is assigned to one or more books",
  "result": null
}
```

---

## GET /api/v1/categories

### 1. Title
- v1 Get All Categories

### 2. Endpoint
- `/api/v1/categories`

### 3. Method
- `GET`

### 4. URL Parameters
- `None`

### 5. Message Payload
- `None`

### 6. Header Parameters
- `Content-Type: application/json`
- `Authorization: Bearer <JWT_TOKEN>`

### 7. Authentication & Authorization Summary
- Authentication: `Public`
- Authorization: `No role restriction`

Note: `SecurityConfig` includes `/api/v1/categories/**` in public GET endpoints.

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Category list returned.

#### ❌ Error Responses
- `500 Internal Server Error` / code `1000`: Unhandled runtime exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X GET "http://localhost:8080/api/v1/categories"
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
  "result": [
    {
      "id": "cat-001",
      "name": "Software Architecture"
    },
    {
      "id": "cat-002",
      "name": "Data Science"
    }
  ]
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
