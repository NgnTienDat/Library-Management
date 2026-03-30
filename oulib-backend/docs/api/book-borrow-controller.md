# BookBorrowController API Documentation

Base path: `/api/v1/books`

Standard response wrapper used by all APIs:

```json
{
  "code": 200,
  "message": "Success",
  "result": {}
}
```

---

## POST /api/v1/books/borrow

### 1. Title
- v1 Borrow Books

### 2. Endpoint
- `/api/v1/books/borrow`

### 3. Method
- `POST`

### 4. URL Parameters
- `None`

### 5. Message Payload
Based on `BorrowRequest`:

| Field | Type | Required | Description |
|---|---|---|---|
| borrowerId | string | false | Borrower user ID. No Bean Validation annotation in DTO. |
| barcodes | array of string | false | List of book-copy barcodes to borrow. Must be unique by business rule. |

Business validations from service:
- `barcodes` must not contain duplicates.
- Borrower must exist and must not be suspended.
- Borrower must not have any overdue borrowing record.
- Requested barcode count must not exceed current borrower quota.
- Each barcode must exist, be available, and not already actively borrowed by the same borrower.

### 6. Header Parameters
- `Content-Type: application/json`
- `Authorization: Bearer <JWT_TOKEN>`

### 7. Authentication & Authorization Summary
- Authentication: `Requires JWT`
- Authorization: `LIBRARIAN` only (enforced at service layer via `@PreAuthorize("hasRole('LIBRARIAN')")`)

### 8. Response Codes

#### ✅ Success Responses
- `201 Created`: Borrow operation completed and records created.

#### ❌ Error Responses
- `401 Unauthorized`
  - Internal code: `1003`
  - Message: `Unauthenticated`
  - Condition: Missing/invalid/expired/blacklisted JWT.
- `403 Forbidden`
  - Internal code: `1037`
  - Message: `You do not have permission to perform this action`
  - Condition: Authenticated user is not `LIBRARIAN`.
- `409 Conflict`
  - Internal code: `1039`
  - Message: `One or more barcodes already exist`
  - Condition: Duplicate values detected inside request `barcodes`.
- `404 Not Found`
  - Internal code: `1001`
  - Message: `User not found`
  - Condition: `borrowerId` does not exist, or authenticated librarian email does not map to a user.
- `403 Forbidden`
  - Internal code: `1041`
  - Message: `User account is inactivated`
  - Condition: Borrower status is `SUSPENDED`.
- `403 Forbidden`
  - Internal code: `1040`
  - Message: `You have overdue books. Please return them before borrowing new ones.`
  - Condition: Borrower currently has an `OVERDUE` borrow record.
- `400 Bad Request`
  - Internal code: `1031`
  - Message: `You have reached your borrowing limit`
  - Condition: Requested number of barcodes exceeds borrower quota.
- `404 Not Found`
  - Internal code: `1030`
  - Message: `Book not found`
  - Condition: Any barcode does not exist in book-copy table.
- `400 Bad Request`
  - Internal code: `1032`
  - Message: `No available copies for this book`
  - Condition: Any targeted copy is not in `AVAILABLE` status.
- `409 Conflict`
  - Internal code: `1033`
  - Message: `You are already borrowing this book`
  - Condition: Borrower already has active `BORROWING` record for that copy.
- `500 Internal Server Error`
  - Internal code: `1000`
  - Message: `Uncategorized Error`
  - Condition: Any unhandled runtime exception (for example null request fields with no Bean Validation guard).

### 9. Sample Calls

#### cURL Example
```bash
curl -X POST "http://localhost:8080/api/v1/books/borrow" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d '{
    "borrowerId": "user-001",
    "barcodes": ["BC-0001", "BC-0002"]
  }'
```

#### Request JSON
```json
{
  "borrowerId": "user-001",
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
      "id": "br-1001",
      "barcode": "BC-0001",
      "borrowDate": "2026-03-25",
      "dueDate": "2026-03-25T12:30:00",
      "returnDate": null,
      "status": "BORROWING"
    },
    {
      "id": "br-1002",
      "barcode": "BC-0002",
      "borrowDate": "2026-03-25",
      "dueDate": "2026-03-25T12:30:00",
      "returnDate": null,
      "status": "BORROWING"
    }
  ]
}
```

#### Error Response JSON
```json
{
  "code": 1040,
  "message": "You have overdue books. Please return them before borrowing new ones.",
  "result": null
}
```

---

## POST /api/v1/books/return

### 1. Title
- v1 Return Borrowed Books

### 2. Endpoint
- `/api/v1/books/return`

### 3. Method
- `POST`

### 4. URL Parameters
- `None`

### 5. Message Payload
Based on `ReturnRequest`:

| Field | Type | Required | Description |
|---|---|---|---|
| barcodes | array of string | false | List of borrowed copy barcodes to return. Must be unique by business rule. |

Business validations from service:
- `barcodes` must not contain duplicates.
- Each barcode must exist and currently be in `BORROWED` status.
- There must be an active `BORROWING` record for each targeted copy.

### 6. Header Parameters
- `Content-Type: application/json`
- `Authorization: Bearer <JWT_TOKEN>`

### 7. Authentication & Authorization Summary
- Authentication: `Requires JWT`
- Authorization: `LIBRARIAN` only (enforced at service layer via `@PreAuthorize("hasRole('LIBRARIAN')")`)

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Return operation completed and records updated.

#### ❌ Error Responses
- `401 Unauthorized`
  - Internal code: `1003`
  - Message: `Unauthenticated`
  - Condition: Missing/invalid/expired/blacklisted JWT.
- `403 Forbidden`
  - Internal code: `1037`
  - Message: `You do not have permission to perform this action`
  - Condition: Authenticated user is not `LIBRARIAN`.
- `400 Bad Request`
  - Internal code: `1043`
  - Message: `Duplicate barcodes found in request`
  - Condition: Duplicate values detected inside request `barcodes`.
- `404 Not Found`
  - Internal code: `1030`
  - Message: `Book not found`
  - Condition: Any barcode does not exist in book-copy table.
- `400 Bad Request`
  - Internal code: `1042`
  - Message: `This book copy is not currently borrowed`
  - Condition: Targeted copy status is not `BORROWED`.
- `404 Not Found`
  - Internal code: `1034`
  - Message: `No active borrow record found for this book`
  - Condition: No `BORROWING` record found for targeted copy.
- `500 Internal Server Error`
  - Internal code: `1000`
  - Message: `Uncategorized Error`
  - Condition: Any unhandled runtime exception (for example null request fields with no Bean Validation guard).

### 9. Sample Calls

#### cURL Example
```bash
curl -X POST "http://localhost:8080/api/v1/books/return" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -d '{
    "barcodes": ["BC-0001", "BC-0002"]
  }'
```

#### Request JSON
```json
{
  "barcodes": ["BC-0001", "BC-0002"]
}
```

#### Success Response JSON
```json
{
  "code": 200,
  "message": "Success",
  "result": [
    {
      "id": "br-1001",
      "barcode": "BC-0001",
      "borrowDate": "2026-03-20",
      "dueDate": "2026-03-25T12:30:00",
      "returnDate": "2026-03-25",
      "status": "RETURNED"
    },
    {
      "id": "br-1002",
      "barcode": "BC-0002",
      "borrowDate": "2026-03-20",
      "dueDate": "2026-03-25T12:30:00",
      "returnDate": "2026-03-25",
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

## GET /api/v1/books/history

### 1. Title
- v1 My Borrowing History

### 2. Endpoint
- `/api/v1/books/history`

### 3. Method
- `GET`

### 4. URL Parameters
| Name | Type | Required | Description |
|---|---|---|---|
| status | string | false | Borrow status filter. Allowed values: `borrowing`, `return`, `overdue` (case-insensitive). |

If `status` is omitted, API returns all records for the authenticated user.

### 5. Message Payload
- `None`

### 6. Header Parameters
- `Authorization: Bearer <JWT_TOKEN>`

### 7. Authentication & Authorization Summary
- Authentication: `Requires JWT`
- Authorization: `USER`, `LIBRARIAN`, `SYSADMIN`

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Borrowing history returned.

#### ❌ Error Responses
- `401 Unauthorized`
  - Internal code: `1003`
  - Message: `Unauthenticated`
  - Condition: Missing/invalid/expired/blacklisted JWT.
- `404 Not Found`
  - Internal code: `1001`
  - Message: `User not found`
  - Condition: JWT subject email does not map to a user.
- `400 Bad Request`
  - Internal code: `2011`
  - Message: `Invalid borrow status. Allowed values: borrowing, return, overdue`
  - Condition: Unsupported `status` value.

### 9. Sample Calls

#### cURL Example (All statuses)
```bash
curl -X GET "http://localhost:8080/api/v1/books/history" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

#### cURL Example (Filter by status)
```bash
curl -X GET "http://localhost:8080/api/v1/books/history?status=borrowing" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

#### Success Response JSON
```json
{
  "code": 200,
  "message": "Success",
  "result": [
    {
      "id": "br-1001",
      "barcode": "BC-0001",
      "borrowDate": "2026-03-20",
      "dueDate": "2026-03-25T12:30:00",
      "returnDate": null,
      "status": "BORROWING"
    },
    {
      "id": "br-1002",
      "barcode": "BC-0002",
      "borrowDate": "2026-03-10",
      "dueDate": "2026-03-17T12:30:00",
      "returnDate": "2026-03-15",
      "status": "RETURNED"
    }
  ]
}
```
