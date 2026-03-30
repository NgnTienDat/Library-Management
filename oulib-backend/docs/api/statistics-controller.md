# StatisticsController API Documentation

Base path: `/api/v1`

Standard response wrapper used by all APIs:

```json
{
  "code": 200,
  "message": "Success",
  "result": {}
}
```

---

## GET /api/v1/statistics/system-totals

### 1. Title
- v1 Get System Totals Statistics

### 2. Endpoint
- `/api/v1/statistics/system-totals`

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
- Authorization: `SYSADMIN` (from `@PreAuthorize("hasRole('SYSADMIN')")`)

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: System totals statistics generated.

#### ❌ Error Responses
- `401 Unauthorized`
  - Internal code: `1003`
  - Message: `Unauthenticated`
  - Condition: Missing/invalid/expired/blacklisted JWT.
- `403 Forbidden`
  - Internal code: `1037`
  - Message: `You do not have permission to perform this action`
  - Condition: Authenticated user does not have `SYSADMIN` role.
- `500 Internal Server Error`
  - Internal code: `1000`
  - Message: `Uncategorized Error`
  - Condition: Any unhandled runtime/database exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X GET "http://localhost:8080/api/v1/statistics/system-totals" \
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
    "totalUsers": 120,
    "totalBooks": 125,
    "totalCopies": 480,
    "totalBorrowRecords": 358,
    "totalCurrentlyBorrowed": 97,
    "totalOverdue": 14
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

## GET /api/v1/statistics/inventory-summary

### 1. Title
- v1 Get Inventory Summary Statistics

### 2. Endpoint
- `/api/v1/statistics/inventory-summary`

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
- Authorization: `SYSADMIN`, `LIBRARIAN` (from `@PreAuthorize("hasAnyRole('SYSADMIN','LIBRARIAN')")`)

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Inventory summary generated.

#### ❌ Error Responses
- `401 Unauthorized`
  - Internal code: `1003`
  - Message: `Unauthenticated`
  - Condition: Missing/invalid/expired/blacklisted JWT.
- `403 Forbidden`
  - Internal code: `1037`
  - Message: `You do not have permission to perform this action`
  - Condition: Authenticated user does not have `SYSADMIN` or `LIBRARIAN` role.
- `500 Internal Server Error`
  - Internal code: `1000`
  - Message: `Uncategorized Error`
  - Condition: Any unhandled runtime/database exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X GET "http://localhost:8080/api/v1/statistics/inventory-summary" \
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
    "totalBooks": 125,
    "totalBookCopies": 480,
    "availableCopies": 340,
    "borrowedCopies": 140,
    "availabilityRate": 0.7083333333333334,
    "categoryDistribution": [
      {
        "categoryId": "cat-001",
        "categoryName": "Software Architecture",
        "bookCount": 38
      },
      {
        "categoryId": "cat-002",
        "categoryName": "Data Science",
        "bookCount": 27
      }
    ]
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

## GET /api/v1/statistics/overdue

### 1. Title
- v1 Get Overdue Borrow Records

### 2. Endpoint
- `/api/v1/statistics/overdue`

### 3. Method
- `GET`

### 4. URL Parameters

| Name | Type | Required | Description |
|---|---|---|---|
| page | integer | false | Page index. Default `0`. |
| size | integer | false | Page size. Default `10`. |

### 5. Message Payload
- `None`

### 6. Header Parameters
- `Content-Type: application/json`
- `Authorization: Bearer <JWT_TOKEN>`

### 7. Authentication & Authorization Summary
- Authentication: `Requires JWT`
- Authorization: `SYSADMIN`, `LIBRARIAN` (from `@PreAuthorize("hasAnyRole('SYSADMIN','LIBRARIAN')")`)

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Paginated overdue users and overdue books returned.

#### ❌ Error Responses
- `401 Unauthorized`
  - Internal code: `1003`
  - Message: `Unauthenticated`
  - Condition: Missing/invalid/expired/blacklisted JWT.
- `403 Forbidden`
  - Internal code: `1037`
  - Message: `You do not have permission to perform this action`
  - Condition: Authenticated user does not have `SYSADMIN` or `LIBRARIAN` role.
- `500 Internal Server Error`
  - Internal code: `1000`
  - Message: `Uncategorized Error`
  - Condition: Invalid pagination values (for example negative page/size) or any unhandled runtime/database exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X GET "http://localhost:8080/api/v1/statistics/overdue?page=0&size=10" \
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
        "userId": "user-001",
        "userName": "Nguyen Van A",
        "email": "a@example.com",
        "overdueBooks": [
          {
            "bookTitle": "Clean Code",
            "barcode": "BC-0021",
            "dueDate": "2026-03-20",
            "overdueDays": 5
          }
        ]
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
  "code": 1003,
  "message": "Unauthenticated",
  "result": null
}
```

---

## GET /api/v1/statistics/borrowing-activity

### 1. Title
- v1 Get Borrowing And Returning Activity Timeline

### 2. Endpoint
- `/api/v1/statistics/borrowing-activity`

### 3. Method
- `GET`

### 4. URL Parameters

| Name | Type | Required | Description |
|---|---|---|---|
| from | date (`yyyy-MM-dd`) | true | Start date (inclusive). |
| to | date (`yyyy-MM-dd`) | true | End date (inclusive). |
| groupBy | string | false | Aggregation unit: `day`, `week`, or `month`. Default `day`. Unknown values fallback to `day`. |

### 5. Message Payload
- `None`

### 6. Header Parameters
- `Content-Type: application/json`
- `Authorization: Bearer <JWT_TOKEN>`

### 7. Authentication & Authorization Summary
- Authentication: `Requires JWT`
- Authorization: `SYSADMIN`, `LIBRARIAN` (from `@PreAuthorize("hasAnyRole('SYSADMIN','LIBRARIAN')")`)

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Borrow/return timeline returned.

Behavior detail from service:
- If `from` is after `to`, service returns an empty list (HTTP 200).

#### ❌ Error Responses
- `401 Unauthorized`
  - Internal code: `1003`
  - Message: `Unauthenticated`
  - Condition: Missing/invalid/expired/blacklisted JWT.
- `403 Forbidden`
  - Internal code: `1037`
  - Message: `You do not have permission to perform this action`
  - Condition: Authenticated user does not have `SYSADMIN` or `LIBRARIAN` role.
- `500 Internal Server Error`
  - Internal code: `1000`
  - Message: `Uncategorized Error`
  - Condition: Invalid date format or any unhandled runtime/database exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X GET "http://localhost:8080/api/v1/statistics/borrowing-activity?from=2026-03-01&to=2026-03-25&groupBy=week" \
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
      "time": "2026-03-02",
      "borrowCount": 12,
      "returnCount": 9
    },
    {
      "time": "2026-03-09",
      "borrowCount": 8,
      "returnCount": 11
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

---

## GET /api/v1/statistics/top-books

### 1. Title
- v1 Get Top Borrowed Books

### 2. Endpoint
- `/api/v1/statistics/top-books`

### 3. Method
- `GET`

### 4. URL Parameters

| Name | Type | Required | Description |
|---|---|---|---|
| limit | integer | false | Number of books to return. Default `10`. If `<= 0`, service uses `10`. |

### 5. Message Payload
- `None`

### 6. Header Parameters
- `Content-Type: application/json`
- `Authorization: None`

### 7. Authentication & Authorization Summary
- Authentication: `Public`
- Authorization: `No role restriction`

Reason:
- Endpoint is in public GET matchers (`/api/v1/statistics/top-books`) and also has `@PreAuthorize("permitAll()")`.

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Ranked top borrowed books returned.

#### ❌ Error Responses
- `500 Internal Server Error`
  - Internal code: `1000`
  - Message: `Uncategorized Error`
  - Condition: Any unhandled runtime/database exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X GET "http://localhost:8080/api/v1/statistics/top-books?limit=5"
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
      "bookId": "book-001",
      "title": "Domain-Driven Design",
      "borrowCount": 128
    },
    {
      "bookId": "book-020",
      "title": "Clean Architecture",
      "borrowCount": 97
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

---

## GET /api/v1/users/active-users

### 1. Title
- v1 Get Active Users Statistics

### 2. Endpoint
- `/api/v1/users/active-users`

### 3. Method
- `GET`

### 4. URL Parameters

| Name | Type | Required | Description |
|---|---|---|---|
| from | date (`yyyy-MM-dd`) | true | Start date (inclusive). |
| to | date (`yyyy-MM-dd`) | true | End date (inclusive). |

### 5. Message Payload
- `None`

### 6. Header Parameters
- `Content-Type: application/json`
- `Authorization: Bearer <JWT_TOKEN>`

### 7. Authentication & Authorization Summary
- Authentication: `Requires JWT`
- Authorization: `SYSADMIN` only (from `@PreAuthorize("hasRole('SYSADMIN')")`)

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Active users and new users statistics returned.

Behavior detail from service:
- If `from` is after `to`, result is returned with zero values:
  - `activeUsers = 0`
  - `newUsers = 0`

#### ❌ Error Responses
- `401 Unauthorized`
  - Internal code: `1003`
  - Message: `Unauthenticated`
  - Condition: Missing/invalid/expired/blacklisted JWT.
- `403 Forbidden`
  - Internal code: `1037`
  - Message: `You do not have permission to perform this action`
  - Condition: Authenticated user is not `SYSADMIN`.
- `500 Internal Server Error`
  - Internal code: `1000`
  - Message: `Uncategorized Error`
  - Condition: Invalid date format or any unhandled runtime/database exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X GET "http://localhost:8080/api/v1/users/active-users?from=2026-03-01&to=2026-03-25" \
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
    "activeUsers": 42,
    "newUsers": 8
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
