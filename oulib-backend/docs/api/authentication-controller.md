# AuthenticationController API Documentation

Base path: `/auth`

Standard response wrapper used by all APIs:

```json
{
  "code": 200,
  "message": "Success",
  "result": {}
}
```

---

## POST /auth/login

### 1. Title
- User Login (Authentication)

### 2. Endpoint
- `/auth/login`

### 3. Method
- `POST`

### 4. URL Parameters
- `None`

### 5. Message Payload
Based on `AuthenticationRequest`:

| Field | Type | Required | Description |
|---|---|---|---|
| email | string | true | User email used to log in. `@NotBlank` |
| password | string | true | User password. `@NotBlank` |

### 6. Header Parameters
- `Content-Type: application/json`
- `Authorization: None`

### 7. Authentication & Authorization Summary
- Authentication: `Public`
- Authorization: `No role restriction`

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Login successful.

#### ❌ Error Responses
- `400 Bad Request`
  - Internal code: `400`
  - Message: `Validation Failed`
  - Condition: Request body validation fails (`email` or `password` blank).
- `404 Not Found`
  - Internal code: `1001`
  - Message: `User not found`
  - Condition: No user found by provided email.
- `400 Bad Request`
  - Internal code: `1006`
  - Message: `Your account is locked`
  - Condition: User status is `SUSPENDED`.
- `401 Unauthorized`
  - Internal code: `1003`
  - Message: `Unauthenticated`
  - Condition: Password does not match.
- `500 Internal Server Error`
  - Internal code: `1000`
  - Message: `Uncategorized Error`
  - Condition: Unexpected runtime error during token generation or other unhandled exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X POST "http://localhost:8080/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "Password@123"
  }'
```

#### Request JSON
```json
{
  "email": "user@example.com",
  "password": "Password@123"
}
```

#### Success Response JSON
```json
{
  "code": 200,
  "message": "Success",
  "result": {
    "authenticated": true,
    "user": {
      "id": "7f9f86ea-4e92-4d2a-8d87-b6df4b8f9d4d",
      "username": "reader01",
      "fullName": "Nguyen Van A",
      "email": "user@example.com",
      "role": "USER",
      "avatar": "https://cdn.example.com/avatar.png",
      "status": "ACTIVE",
      "active": true,
      "createdAt": "2026-03-25T07:45:11Z"
    },
    "token": "<JWT_TOKEN>"
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

## POST /auth/logout

### 1. Title
- Logout Current Session Token

### 2. Endpoint
- `/auth/logout`

### 3. Method
- `POST`

### 4. URL Parameters
- `None`

### 5. Message Payload
Based on `LogoutRequest`:

| Field | Type | Required | Description |
|---|---|---|---|
| token | string | false | JWT token to blacklist. No Bean Validation annotations in DTO/controller. |

### 6. Header Parameters
- `Content-Type: application/json`
- `Authorization: Bearer <JWT_TOKEN>`

### 7. Authentication & Authorization Summary
- Authentication: `Requires JWT`
- Authorization: `Any authenticated role` (`USER`, `LIBRARIAN`, `SYSADMIN`)

Note: Endpoint path is included in public POST matchers, but controller has `@PreAuthorize("isAuthenticated()")`, so an authenticated principal is still required to execute the method.

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Logout processed.

Behavior detail: if the token in request body is expired/invalid and throws `AppException` in service verification, service catches it and still returns success.

#### ❌ Error Responses
- `401 Unauthorized`
  - Internal code: `1003`
  - Message: `Unauthenticated`
  - Condition: Missing/invalid JWT in security layer before method execution.
- `403 Forbidden`
  - Internal code: `1037`
  - Message: `You do not have permission to perform this action`
  - Condition: Access denied by method security (`isAuthenticated()` check fails).
- `500 Internal Server Error`
  - Internal code: `1000`
  - Message: `Uncategorized Error`
  - Condition: Malformed token in body causes parse/JOSE or other unhandled exception propagated from service.

### 9. Sample Calls

#### cURL Example
```bash
curl -X POST "http://localhost:8080/auth/logout" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d '{
    "token": "<JWT_TOKEN>"
  }'
```

#### Request JSON
```json
{
  "token": "<JWT_TOKEN>"
}
```

#### Success Response JSON
```json
{
  "code": 200,
  "message": "Logout",
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

---

## POST /auth/introspect

### 1. Title
- Introspect Token Validity

### 2. Endpoint
- `/auth/introspect`

### 3. Method
- `POST`

### 4. URL Parameters
- `None`

### 5. Message Payload
Based on `IntrospectRequest`:

| Field | Type | Required | Description |
|---|---|---|---|
| token | string | true | JWT token to check. `@NotBlank` |

### 6. Header Parameters
- `Content-Type: application/json`
- `Authorization: None`

### 7. Authentication & Authorization Summary
- Authentication: `Public`
- Authorization: `No role restriction`

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Introspection completed.

Behavior detail: if token verification throws `AppException` (invalid signature/expired/blacklisted), service returns `valid: false` with HTTP 200.

#### ❌ Error Responses
- `400 Bad Request`
  - Internal code: `400`
  - Message: `Validation Failed`
  - Condition: Missing/blank `token` field.
  - Validation result detail: field-level message is resolved to `Cannot blank this field` for `@NotBlank`.
- `500 Internal Server Error`
  - Internal code: `1000`
  - Message: `Uncategorized Error`
  - Condition: Parse/JOSE exception not wrapped as `AppException`.

### 9. Sample Calls

#### cURL Example
```bash
curl -X POST "http://localhost:8080/auth/introspect" \
  -H "Content-Type: application/json" \
  -d '{
    "token": "<JWT_TOKEN>"
  }'
```

#### Request JSON
```json
{
  "token": "<JWT_TOKEN>"
}
```

#### Success Response JSON
```json
{
  "code": 200,
  "message": "Introspection Successful",
  "result": {
    "valid": true
  }
}
```

#### Error Response JSON
```json
{
  "code": 400,
  "message": "Validation Failed",
  "result": {
    "token": "Cannot blank this field"
  }
}
```

---

## POST /auth/refresh

### 1. Title
- Refresh Access Token

### 2. Endpoint
- `/auth/refresh`

### 3. Method
- `POST`

### 4. URL Parameters
- `None`

### 5. Message Payload
Based on `RefreshRequest`:

| Field | Type | Required | Description |
|---|---|---|---|
| token | string | false | Existing JWT to be refreshed. No Bean Validation annotations in DTO/controller. |

### 6. Header Parameters
- `Content-Type: application/json`
- `Authorization: Bearer <JWT_TOKEN>`

### 7. Authentication & Authorization Summary
- Authentication: `Requires JWT`
- Authorization: `Any authenticated role` (`USER`, `LIBRARIAN`, `SYSADMIN`)

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Token refreshed successfully.

#### ❌ Error Responses
- `401 Unauthorized`
  - Internal code: `1003`
  - Message: `Unauthenticated`
  - Condition: Missing/invalid JWT in security layer, invalid refresh token, blacklisted token, expired refresh window, or user from token subject not found.
- `500 Internal Server Error`
  - Internal code: `1000`
  - Message: `Uncategorized Error`
  - Condition: Malformed token parse/JOSE or any unhandled runtime exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X POST "http://localhost:8080/auth/refresh" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d '{
    "token": "<JWT_TOKEN>"
  }'
```

#### Request JSON
```json
{
  "token": "<JWT_TOKEN>"
}
```

#### Success Response JSON
```json
{
  "code": 200,
  "message": "Refresh token",
  "result": {
    "authenticated": true,
    "user": {
      "id": "7f9f86ea-4e92-4d2a-8d87-b6df4b8f9d4d",
      "username": "reader01",
      "fullName": "Nguyen Van A",
      "email": "user@example.com",
      "role": "USER",
      "avatar": "https://cdn.example.com/avatar.png",
      "status": "ACTIVE",
      "active": true,
      "createdAt": "2026-03-25T07:45:11Z"
    },
    "token": "<NEW_JWT_TOKEN>"
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
