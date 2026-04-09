# BookBorrowController API Documentation

Base path: `/api/v1/borrowing`

Standard response wrapper used by all APIs:

```json
{
  "code": 200,
  "message": "Success",
  "result": {}
}
```

---

## POST /api/v1/borrowing

### 1. Title
- v1 Borrow Books

### 2. Endpoint
- `/api/v1/borrowing`

### 3. Method
- `POST`

### 4. URL Parameters
- `None`

### 5. Message Payload
Based on `BorrowRequest`:

| Field | Type | Required | Description |
|---|---|---|---|
| borrowerId | string | false | Borrower user ID. |
| borrowDuration | integer | true | Borrow duration in days. `@NotNull`, `@Min(0)`. |
| barcodes | array of string | false | Book-copy barcodes to borrow. Must be unique in request. |

### 6. Header Parameters
- `Content-Type: application/json`
- `Authorization: Bearer <JWT_TOKEN>`

### 7. Authentication & Authorization Summary
- Authentication: `Requires JWT`
- Authorization: `LIBRARIAN` only

### 8. Response Codes

#### ✅ Success Responses
- `201 Created`: Borrow records created.

#### ❌ Error Responses
- `400 Bad Request` / code `400`
  - Message: `Validation Failed`
  - Condition: `borrowDuration` missing or invalid by Bean Validation.
- `401 Unauthorized` / code `1003`: Missing/invalid JWT.
- `403 Forbidden` / code `1037`: Role is not `LIBRARIAN`.
- `409 Conflict` / code `1039`: Duplicate barcodes in request.
- `404 Not Found` / code `1001`: Borrower not found or authenticated librarian not found.
- `403 Forbidden` / code `1041`: Borrower is suspended/inactive.
- `403 Forbidden` / code `1040`: Borrower has overdue books.
- `400 Bad Request` / code `1031`: Borrow request exceeds borrower quota.
- `404 Not Found` / code `1030`: Barcode not found.
- `400 Bad Request` / code `1032`: Copy is not available.
- `409 Conflict` / code `1033`: Borrower already borrowing the same copy.
- `500 Internal Server Error` / code `1000`: Any unhandled runtime exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X POST "http://localhost:8080/api/v1/borrowing" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d '{"borrowerId":"user-001","borrowDuration":14,"barcodes":["BC-0001","BC-0002"]}'
```

#### Request JSON
```json
{
  "borrowerId": "user-001",
  "borrowDuration": 14,
  "barcodes": ["BC-0001", "BC-0002"]
}
```

#### Success Response JSON
```json
{
  "code": 201,
  "message": "Created",
  "result": [
    {
      "id": "br-001",
      "borrowerId": "user-001",
      "barcode": "BC-0001",
      "borrowDate": "2026-04-08",
      "dueDate": "2026-04-22T00:00:00",
      "returnDate": null,
      "status": "BORROWING"
    }
  ]
}
```

#### Error Response JSON
```json
{
  "code": 1031,
  "message": "You have reached your borrowing limit",
  "result": null
}
```

---

## POST /api/v1/borrowing/return

### 1. Title
- v1 Return Borrowed Books

### 2. Endpoint
- `/api/v1/borrowing/return`

### 3. Method
- `POST`

### 4. URL Parameters
- `None`

### 5. Message Payload
Based on `ReturnRequest`:

| Field | Type | Required | Description |
|---|---|---|---|
| barcodes | array of string | false | Book-copy barcodes to return. Must be unique in request. |

### 6. Header Parameters
- `Content-Type: application/json`
- `Authorization: Bearer <JWT_TOKEN>`

### 7. Authentication & Authorization Summary
- Authentication: `Requires JWT`
- Authorization: `LIBRARIAN` only

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Return operation completed.

#### ❌ Error Responses
- `401 Unauthorized` / code `1003`: Missing/invalid JWT.
- `403 Forbidden` / code `1037`: Role is not `LIBRARIAN`.
- `400 Bad Request` / code `1043`: Duplicate barcodes in request.
- `404 Not Found` / code `1030`: Barcode not found.
- `400 Bad Request` / code `1042`: Copy is not currently borrowed.
- `404 Not Found` / code `1034`: Active borrowing record not found.
- `500 Internal Server Error` / code `1000`: Any unhandled runtime exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X POST "http://localhost:8080/api/v1/borrowing/return" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d '{"barcodes":["BC-0001"]}'
```

#### Request JSON
```json
{
  "barcodes": ["BC-0001"]
}
```

#### Success Response JSON
```json
{
  "code": 200,
  "message": "Success",
  "result": [
    {
      "id": "br-001",
      "borrowerId": "user-001",
      "barcode": "BC-0001",
      "borrowDate": "2026-04-01",
      "dueDate": "2026-04-15T00:00:00",
      "returnDate": "2026-04-08",
      "status": "RETURNED"
    }
  ]
}
```

#### Error Response JSON
```json
{
  "code": 1042,
  "message": "This book copy is not currently borrowed",
  "result": null
}
```

---

## GET /api/v1/borrowing/history

### 1. Title
- v1 Get My Borrowing History

### 2. Endpoint
- `/api/v1/borrowing/history`

### 3. Method
- `GET`

### 4. URL Parameters

| Name | Type | Required | Description |
|---|---|---|---|
| status | string | false | Allowed: `borrowing`, `return`/`returned`, `overdue` (case-insensitive). |

### 5. Message Payload
- `None`

### 6. Header Parameters
- `Content-Type: application/json`
- `Authorization: Bearer <JWT_TOKEN>`

### 7. Authentication & Authorization Summary
- Authentication: `Requires JWT`
- Authorization: `USER`, `LIBRARIAN`, `SYSADMIN`

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Authenticated user borrowing history returned.

#### ❌ Error Responses
- `401 Unauthorized` / code `1003`: Missing/invalid JWT.
- `404 Not Found` / code `1001`: Authenticated user does not exist.
- `400 Bad Request` / code `2011`: Invalid status value.
- `500 Internal Server Error` / code `1000`: Any unhandled runtime exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X GET "http://localhost:8080/api/v1/borrowing/history?status=borrowing" \
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
  "result": [
    {
      "id": "br-001",
      "borrowerId": "user-001",
      "barcode": "BC-0001",
      "borrowDate": "2026-04-01",
      "dueDate": "2026-04-15T00:00:00",
      "returnDate": null,
      "status": "BORROWING"
    }
  ]
}
```

#### Error Response JSON
```json
{
  "code": 2011,
  "message": "Invalid borrow status. Allowed values: borrowing, return, overdue",
  "result": null
}
```

---

## GET /api/v1/borrowing/{userId}/history

### 1. Title
- v1 Get User Borrowing History

### 2. Endpoint
- `/api/v1/borrowing/{userId}/history`

### 3. Method
- `GET`

### 4. URL Parameters

| Name | Type | Required | Description |
|---|---|---|---|
| userId | string | true | Target user ID. |
| status | string | false | Allowed: `borrowing`, `return`/`returned`, `overdue`. |

### 5. Message Payload
- `None`

### 6. Header Parameters
- `Content-Type: application/json`
- `Authorization: Bearer <JWT_TOKEN>`

### 7. Authentication & Authorization Summary
- Authentication: `Requires JWT`
- Authorization: `LIBRARIAN`, `SYSADMIN`

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Target user borrowing history returned.

#### ❌ Error Responses
- `401 Unauthorized` / code `1003`: Missing/invalid JWT.
- `403 Forbidden` / code `1037`: Role is neither `LIBRARIAN` nor `SYSADMIN`.
- `404 Not Found` / code `1001`: Target user does not exist.
- `400 Bad Request` / code `2011`: Invalid status value.
- `500 Internal Server Error` / code `1000`: Any unhandled runtime exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X GET "http://localhost:8080/api/v1/borrowing/user-001/history?status=returned" \
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
  "result": []
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

## GET /api/v1/borrowing/records

### 1. Title
- v1 Get Borrow Records (Paginated)

### 2. Endpoint
- `/api/v1/borrowing/records`

### 3. Method
- `GET`

### 4. URL Parameters

| Name | Type | Required | Description |
|---|---|---|---|
| status | string | false | Allowed: `borrowing`, `return`/`returned`, `overdue`. |
| borrowerId | string | false | Filter by borrower ID. |
| page | integer | false | Default `0`. |
| size | integer | false | Default `10`. |

### 5. Message Payload
- `None`

### 6. Header Parameters
- `Content-Type: application/json`
- `Authorization: Bearer <JWT_TOKEN>`

### 7. Authentication & Authorization Summary
- Authentication: `Requires JWT`
- Authorization: `LIBRARIAN`, `SYSADMIN`

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Paginated borrow records returned.

#### ❌ Error Responses
- `401 Unauthorized` / code `1003`: Missing/invalid JWT.
- `403 Forbidden` / code `1037`: Role is neither `LIBRARIAN` nor `SYSADMIN`.
- `400 Bad Request` / code `2011`: Invalid status value.
- `500 Internal Server Error` / code `1000`: Invalid paging values or any unhandled exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X GET "http://localhost:8080/api/v1/borrowing/records?status=borrowing&page=0&size=10" \
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
        "id": "br-001",
        "borrowerId": "user-001",
        "barcode": "BC-0001",
        "borrowDate": "2026-04-01",
        "dueDate": "2026-04-15T00:00:00",
        "returnDate": null,
        "status": "BORROWING"
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
  "code": 2011,
  "message": "Invalid borrow status. Allowed values: borrowing, return, overdue",
  "result": null
}
```

---

## GET /api/v1/borrowing/records/{recordId}

### 1. Title
- v1 Get Borrow Record Detail

### 2. Endpoint
- `/api/v1/borrowing/records/{recordId}`

### 3. Method
- `GET`

### 4. URL Parameters

| Name | Type | Required | Description |
|---|---|---|---|
| recordId | string | true | Borrow record ID. |

### 5. Message Payload
- `None`

### 6. Header Parameters
- `Content-Type: application/json`
- `Authorization: Bearer <JWT_TOKEN>`

### 7. Authentication & Authorization Summary
- Authentication: `Requires JWT`
- Authorization: `LIBRARIAN`, `SYSADMIN`

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Borrow record detail returned.

#### ❌ Error Responses
- `401 Unauthorized` / code `1003`: Missing/invalid JWT.
- `403 Forbidden` / code `1037`: Role is neither `LIBRARIAN` nor `SYSADMIN`.
- `404 Not Found` / code `1034`: Borrow record not found.
- `500 Internal Server Error` / code `1000`: Any unhandled runtime exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X GET "http://localhost:8080/api/v1/borrowing/records/br-001" \
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
    "id": "br-001",
    "borrowerId": "user-001",
    "borrowerFullName": "Nguyen Van A",
    "borrowerEmail": "a@example.com",
    "barcode": "BC-0001",
    "borrowDate": "2026-04-01T02:30:00Z",
    "dueDate": "2026-04-15",
    "status": "BORROWING"
  }
}
```

#### Error Response JSON
```json
{
  "code": 1034,
  "message": "No active borrow record found for this book",
  "result": null
}
```
