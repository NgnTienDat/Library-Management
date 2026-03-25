# UserController API Documentation

Base path: `/api/v1/users`

Standard response wrapper used by all APIs:

```json
{
  "code": 200,
  "message": "Success",
  "result": {}
}
```

---

## POST /api/v1/users

### 1. Title
- v1 Create/Register User Account 

### 2. Endpoint
- `/api/v1/users`

### 3. Method
- `POST`

### 4. URL Parameters
- `None`

### 5. Message Payload
Based on `UserCreationRequest`:

| Field | Type | Required | Description |
|---|---|---|---|
| email | string | false | User email. Validated with `@Email(message = "INVALID_EMAIL")` if provided. |
| fullName | string | true | Full name. `@NotBlank`, length `1..50`. |
| password | string | true | Account password. `@NotBlank`, min length `6`. |

### 6. Header Parameters
- `Content-Type: application/json`
- `Authorization: None`

### 7. Authentication & Authorization Summary
- Authentication: `Public`
- Authorization: `No role restriction`

### 8. Response Codes

#### ✅ Success Responses
- `201 Created`: User account created successfully.

#### ❌ Error Responses
- `400 Bad Request`
  - Internal code: `400`
  - Message: `Validation Failed`
  - Condition: Bean validation fails for request body.
- `400 Bad Request`
  - Internal code: `400`
  - Message: `Validation Failed`
  - Condition: Validation message key is not mapped in `ErrorCode` (for example `fullName` constraints use plain text message keys), resulting in field message `Invalid Message Key`.
- `409 Conflict`
  - Internal code: `1002`
  - Message: `User already exists`
  - Condition: Email already exists (`existsByEmail`) or unique constraint conflict on save.
- `500 Internal Server Error`
  - Internal code: `1000`
  - Message: `Uncategorized Error`
  - Condition: Any unhandled runtime exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X POST "http://localhost:8080/api/v1/users" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "new.user@example.com",
    "fullName": "New User",
    "password": "Password@123"
  }'
```

#### Request JSON
```json
{
  "email": "new.user@example.com",
  "fullName": "New User",
  "password": "Password@123"
}
```

#### Success Response JSON
```json
{
  "code": 201,
  "message": "Created",
  "result": {
    "id": "f5dfc41e-920b-4d50-a9ec-90ce0410f1a0",
    "username": null,
    "fullName": "New User",
    "email": "new.user@example.com",
    "role": "USER",
    "avatar": null,
    "status": "ACTIVE",
    "active": false,
    "createdAt": "2026-03-25T09:10:00Z"
  }
}
```

#### Error Response JSON
```json
{
  "code": 1002,
  "message": "User already exists",
  "result": null
}
```

---

## GET /api/v1/users/me

### 1. Title
- v1 Get Current User Profile

### 2. Endpoint
- `/api/v1/users/me`

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
- Authentication: `Requires JWT`
- Authorization: `Any authenticated role` (`USER`, `LIBRARIAN`, `SYSADMIN`)

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Current authenticated user profile retrieved.

#### ❌ Error Responses
- `401 Unauthorized`
  - Internal code: `1003`
  - Message: `Unauthenticated`
  - Condition: Missing/invalid/expired/blacklisted JWT.
- `404 Not Found`
  - Internal code: `1001`
  - Message: `User not found`
  - Condition: JWT subject email does not map to an existing user.
- `500 Internal Server Error`
  - Internal code: `1000`
  - Message: `Uncategorized Error`
  - Condition: Any unhandled runtime exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X GET "http://localhost:8080/api/v1/users/me" \
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
  "result": {
    "id": "f5dfc41e-920b-4d50-a9ec-90ce0410f1a0",
    "username": "reader01",
    "fullName": "New User",
    "email": "new.user@example.com",
    "role": "USER",
    "avatar": "https://cdn.example.com/avatar.png",
    "status": "ACTIVE",
    "active": true,
    "createdAt": "2026-03-25T09:10:00Z"
  }
}
```

#### Error Response JSON
```json
{
  "code": 1003,
  "message": "Unauthenticated",
  "result": null
}
```

---

## PUT /api/v1/users/me

### 1. Title
- v1 Update Current User Profile

### 2. Endpoint
- `/api/v1/users/me`

### 3. Method
- `PUT`

### 4. URL Parameters
- `None`

### 5. Message Payload
Consumes `multipart/form-data` with request parts:

| Field | Type | Required | Description |
|---|---|---|---|
| data | object (`UserUpdateRequest`) | true | JSON part containing profile fields. |
| data.fullName | string | false | Full name, length `1..50` if provided. |
| data.username | string | false | Username, length `2..100` if provided. |
| data.avatar | string | false | Avatar URL string in DTO. |
| avatar | file | false | Uploaded avatar file part (`MultipartFile`). |

Current service behavior:
- Only `data.fullName` is applied when changed.
- `data.username`, `data.avatar`, and file part `avatar` are currently accepted but not persisted by `updateMyProfile`.

### 6. Header Parameters
- `Content-Type: multipart/form-data`
- `Authorization: Bearer <JWT_TOKEN>`

### 7. Authentication & Authorization Summary
- Authentication: `Requires JWT`
- Authorization: `Any authenticated role` (`USER`, `LIBRARIAN`, `SYSADMIN`)

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Profile update request processed.

#### ❌ Error Responses
- `401 Unauthorized`
  - Internal code: `1003`
  - Message: `Unauthenticated`
  - Condition: Missing/invalid/expired/blacklisted JWT.
- `400 Bad Request`
  - Internal code: `400`
  - Message: `Validation Failed`
  - Condition: Bean validation fails on `data` request part.
- `400 Bad Request`
  - Internal code: `400`
  - Message: `Validation Failed`
  - Condition: Validation message key is not mapped in `ErrorCode` (for example username/fullName `@Size` messages), resulting in field message `Invalid Message Key`.
- `404 Not Found`
  - Internal code: `1001`
  - Message: `User not found`
  - Condition: JWT subject email does not map to an existing user.
- `500 Internal Server Error`
  - Internal code: `1000`
  - Message: `Uncategorized Error`
  - Condition: Missing required multipart part `data` or any other unhandled runtime exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X PUT "http://localhost:8080/api/v1/users/me" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -F 'data={"fullName":"Updated Name","username":"reader02"};type=application/json' \
  -F "avatar=@C:/tmp/avatar.png"
```

#### Request JSON
```json
{
  "fullName": "Updated Name",
  "username": "reader02",
  "avatar": "https://cdn.example.com/new-avatar.png"
}
```

#### Success Response JSON
```json
{
  "code": 200,
  "message": "Success",
  "result": {
    "id": "f5dfc41e-920b-4d50-a9ec-90ce0410f1a0",
    "username": "reader01",
    "fullName": "Updated Name",
    "email": "new.user@example.com",
    "role": "USER",
    "avatar": "https://cdn.example.com/avatar.png",
    "status": "ACTIVE",
    "active": true,
    "createdAt": "2026-03-25T09:10:00Z"
  }
}
```

#### Error Response JSON
```json
{
  "code": 400,
  "message": "Validation Failed",
  "result": {
    "username": "Invalid Message Key"
  }
}
```
