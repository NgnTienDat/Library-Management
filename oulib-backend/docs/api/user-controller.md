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
- v1 Register User Account

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
| email | string | true | `@NotBlank`, `@Email`. |
| fullName | string | true | `@NotBlank`, length 1..50. |
| password | string | true | `@NotBlank`, min length 6. |

### 6. Header Parameters
- `Content-Type: application/json`
- `Authorization: None`

### 7. Authentication & Authorization Summary
- Authentication: `Public`
- Authorization: `No role restriction`

### 8. Response Codes

#### ✅ Success Responses
- `201 Created`: User created.

#### ❌ Error Responses
- `400 Bad Request` / code `400`: Validation failed.
- `409 Conflict` / code `1002`: User already exists.
- `500 Internal Server Error` / code `1000`: Unhandled runtime exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X POST "http://localhost:8080/api/v1/users" \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","fullName":"User Name","password":"Password@123"}'
```

#### Request JSON
```json
{
  "email": "user@example.com",
  "fullName": "User Name",
  "password": "Password@123"
}
```

#### Success Response JSON
```json
{
  "code": 201,
  "message": "Created",
  "result": {
    "id": "u-001",
    "username": null,
    "fullName": "User Name",
    "email": "user@example.com",
    "role": "USER",
    "avatar": null,
    "status": "ACTIVE",
    "active": false,
    "createdAt": "2026-04-08T08:00:00Z"
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

## POST /api/v1/users/staff

### 1. Title
- v1 Create Staff Account

### 2. Endpoint
- `/api/v1/users/staff`

### 3. Method
- `POST`

### 4. URL Parameters
- `None`

### 5. Message Payload
Based on `StaffCreationRequest`:

| Field | Type | Required | Description |
|---|---|---|---|
| email | string | true | `@NotBlank`, `@Email`. |
| password | string | true | `@NotBlank`, min length 6. |
| fullName | string | true | `@NotBlank`, length 1..50. |
| role | enum | true | `SYSADMIN` or `LIBRARIAN`. |

### 6. Header Parameters
- `Content-Type: application/json`
- `Authorization: Bearer <JWT_TOKEN>`

### 7. Authentication & Authorization Summary
- Authentication: `Requires JWT`
- Authorization: `SYSADMIN` only

### 8. Response Codes

#### ✅ Success Responses
- `201 Created`: Staff account created.

#### ❌ Error Responses
- `400 Bad Request` / code `400`: Validation failed.
- `400 Bad Request` / code `2010`: Invalid role.
- `401 Unauthorized` / code `1003`: Missing/invalid JWT.
- `403 Forbidden` / code `1037`: Role is not `SYSADMIN`.
- `409 Conflict` / code `1002`: User already exists.
- `500 Internal Server Error` / code `1000`: Unhandled runtime exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X POST "http://localhost:8080/api/v1/users/staff" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"email":"librarian@example.com","password":"Password@123","fullName":"Library Staff","role":"LIBRARIAN"}'
```

#### Request JSON
```json
{
  "email": "librarian@example.com",
  "password": "Password@123",
  "fullName": "Library Staff",
  "role": "LIBRARIAN"
}
```

#### Success Response JSON
```json
{
  "code": 201,
  "message": "Created",
  "result": {
    "id": "u-002",
    "username": null,
    "fullName": "Library Staff",
    "email": "librarian@example.com",
    "role": "LIBRARIAN",
    "avatar": null,
    "status": "ACTIVE",
    "active": false,
    "createdAt": "2026-04-08T08:10:00Z"
  }
}
```

#### Error Response JSON
```json
{
  "code": 2010,
  "message": "Invalid role",
  "result": null
}
```

---

## GET /api/v1/users

### 1. Title
- v1 Get Users (Paginated)

### 2. Endpoint
- `/api/v1/users`

### 3. Method
- `GET`

### 4. URL Parameters

| Name | Type | Required | Description |
|---|---|---|---|
| page | integer | false | Default `0`. |
| size | integer | false | Default `10`. |

### 5. Message Payload
- `None`

### 6. Header Parameters
- `Content-Type: application/json`
- `Authorization: Bearer <JWT_TOKEN>`

### 7. Authentication & Authorization Summary
- Authentication: `Requires JWT`
- Authorization: `SYSADMIN`, `LIBRARIAN`

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Paginated users returned.

#### ❌ Error Responses
- `401 Unauthorized` / code `1003`: Missing/invalid JWT.
- `403 Forbidden` / code `1037`: Missing required role.
- `500 Internal Server Error` / code `1000`: Invalid paging values or unhandled runtime exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X GET "http://localhost:8080/api/v1/users?page=0&size=10" \
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
    "content": [
      {
        "id": "u-001",
        "username": null,
        "fullName": "User Name",
        "email": "user@example.com",
        "role": "USER",
        "avatar": null,
        "status": "ACTIVE",
        "active": false,
        "createdAt": "2026-04-08T08:00:00Z"
      }
    ],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 1,
    "totalPages": 1,
    "first": true,
    "last": true,
    "empty": false
  }
}
```

#### Error Response JSON
```json
{
  "code": 1037,
  "message": "You do not have permission to perform this action",
  "result": null
}
```

---

## PATCH /api/v1/users/{id}/status

### 1. Title
- v1 Update User Status

### 2. Endpoint
- `/api/v1/users/{id}/status`

### 3. Method
- `PATCH`

### 4. URL Parameters

| Name | Type | Required | Description |
|---|---|---|---|
| id | string | true | User ID. |

### 5. Message Payload
Based on `UserStatusUpdateRequest`:

| Field | Type | Required | Description |
|---|---|---|---|
| status | enum | true | Target status value (`ACTIVE`, `SUSPENDED`, `DELETED`, `PRIVATE`). |

### 6. Header Parameters
- `Content-Type: application/json`
- `Authorization: Bearer <JWT_TOKEN>`

### 7. Authentication & Authorization Summary
- Authentication: `Requires JWT`
- Authorization: `SYSADMIN`, `LIBRARIAN`

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Status updated.

#### ❌ Error Responses
- `400 Bad Request` / code `400`: Validation failed.
- `401 Unauthorized` / code `1003`: Missing/invalid JWT.
- `403 Forbidden` / code `1037`: Missing required role.
- `404 Not Found` / code `1001`: User not found.
- `500 Internal Server Error` / code `1000`: Unhandled runtime exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X PATCH "http://localhost:8080/api/v1/users/u-001/status" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"status":"SUSPENDED"}'
```

#### Request JSON
```json
{
  "status": "SUSPENDED"
}
```

#### Success Response JSON
```json
{
  "code": 200,
  "message": "Success",
  "result": {
    "id": "u-001",
    "username": null,
    "fullName": "User Name",
    "email": "user@example.com",
    "role": "USER",
    "avatar": null,
    "status": "SUSPENDED",
    "active": false,
    "createdAt": "2026-04-08T08:00:00Z"
  }
}
```

#### Error Response JSON
```json
{
  "code": 1001,
  "message": "User not found",
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
- `200 OK`: Current user returned.

#### ❌ Error Responses
- `401 Unauthorized` / code `1003`: Missing/invalid JWT.
- `404 Not Found` / code `1001`: User not found.
- `500 Internal Server Error` / code `1000`: Unhandled runtime exception.

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
    "id": "u-001",
    "username": null,
    "fullName": "User Name",
    "email": "user@example.com",
    "role": "USER",
    "avatar": null,
    "status": "ACTIVE",
    "active": false,
    "createdAt": "2026-04-08T08:00:00Z"
  }
}
```

#### Error Response JSON
```json
{
  "code": 1001,
  "message": "User not found",
  "result": null
}
```

---

## PUT /api/v1/users/me

### 1. Title
- v1 Update My Profile

### 2. Endpoint
- `/api/v1/users/me`

### 3. Method
- `PUT`

### 4. URL Parameters
- `None`

### 5. Message Payload
Consumes `multipart/form-data`:

| Field | Type | Required | Description |
|---|---|---|---|
| data | object (`UserUpdateRequest`) | true | JSON profile payload. |
| data.fullName | string | false | Full name, size 1..50 if provided. |
| data.username | string | false | Username, size 2..100 if provided. |
| data.avatar | string | false | Avatar URL field from DTO. |
| avatar | file | false | Multipart avatar file. |

Current behavior in service:
- Only `fullName` is persisted.
- `username`, `data.avatar`, and file `avatar` are accepted but currently not persisted.

### 6. Header Parameters
- `Content-Type: multipart/form-data`
- `Authorization: Bearer <JWT_TOKEN>`

### 7. Authentication & Authorization Summary
- Authentication: `Requires JWT`
- Authorization: `Any authenticated role` (`USER`, `LIBRARIAN`, `SYSADMIN`)

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Profile update processed.

#### ❌ Error Responses
- `400 Bad Request` / code `400`: Validation failed for request part `data`.
- `401 Unauthorized` / code `1003`: Missing/invalid JWT.
- `404 Not Found` / code `1001`: User not found.
- `500 Internal Server Error` / code `1000`: Missing `data` part or unhandled runtime exception.

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
    "id": "u-001",
    "username": null,
    "fullName": "Updated Name",
    "email": "user@example.com",
    "role": "USER",
    "avatar": null,
    "status": "ACTIVE",
    "active": false,
    "createdAt": "2026-04-08T08:00:00Z"
  }
}
```

#### Error Response JSON
```json
{
  "code": 400,
  "message": "Validation Failed",
  "result": {
    "fullName": "Invalid Message Key"
  }
}
```

---

## PUT /api/v1/users/me/password

### 1. Title
- v1 Change My Password

### 2. Endpoint
- `/api/v1/users/me/password`

### 3. Method
- `PUT`

### 4. URL Parameters
- `None`

### 5. Message Payload
Based on `ChangePasswordRequest`:

| Field | Type | Required | Description |
|---|---|---|---|
| oldPassword | string | true | Current password. `@NotBlank`. |
| newPassword | string | true | New password. `@NotBlank`, min length 6. |

### 6. Header Parameters
- `Content-Type: application/json`
- `Authorization: Bearer <JWT_TOKEN>`

### 7. Authentication & Authorization Summary
- Authentication: `Requires JWT`
- Authorization: `Any authenticated role` (`USER`, `LIBRARIAN`, `SYSADMIN`)

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Password changed successfully.

#### ❌ Error Responses
- `400 Bad Request` / code `400`: Validation failed.
- `401 Unauthorized` / code `1003`: Missing/invalid JWT or old password mismatch.
- `404 Not Found` / code `1001`: Authenticated user not found.
- `500 Internal Server Error` / code `1000`: Unhandled runtime exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X PUT "http://localhost:8080/api/v1/users/me/password" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"oldPassword":"OldPass@123","newPassword":"NewPass@123"}'
```

#### Request JSON
```json
{
  "oldPassword": "OldPass@123",
  "newPassword": "NewPass@123"
}
```

#### Success Response JSON
```json
{
  "code": 200,
  "message": "Password changed successfully",
  "result": null
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
