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

## GET /api/v1/statistics/inventory-summary

### 1. Title
- v1 Get Inventory Summary

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
- Authorization: `SYSADMIN`, `LIBRARIAN`

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Inventory summary returned.

#### ❌ Error Responses
- `401 Unauthorized` / code `1003`: Missing/invalid JWT.
- `403 Forbidden` / code `1037`: Missing required role.
- `500 Internal Server Error` / code `1000`: Unhandled runtime/database exception.

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
- v1 Get Overdue Records

### 2. Endpoint
- `/api/v1/statistics/overdue`

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
- `200 OK`: Paginated overdue users with overdue books returned.

#### ❌ Error Responses
- `401 Unauthorized` / code `1003`: Missing/invalid JWT.
- `403 Forbidden` / code `1037`: Missing required role.
- `500 Internal Server Error` / code `1000`: Invalid paging values or unhandled exception.

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
        "userId": "u-001",
        "userName": "Nguyen Van A",
        "email": "a@example.com",
        "overdueBooks": [
          {
            "bookTitle": "Clean Code",
            "barcode": "BC-0021",
            "dueDate": "2026-04-01",
            "overdueDays": 7
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
- v1 Get Borrowing Activity Timeline

### 2. Endpoint
- `/api/v1/statistics/borrowing-activity`

### 3. Method
- `GET`

### 4. URL Parameters

| Name | Type | Required | Description |
|---|---|---|---|
| from | date (`yyyy-MM-dd`) | true | Start date (inclusive). |
| to | date (`yyyy-MM-dd`) | true | End date (inclusive). |
| groupBy | string | false | `day`, `week`, or `month`. Default `day`. |

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
- `200 OK`: Timeline returned.
- If `from > to`, service returns empty list with 200.

#### ❌ Error Responses
- `401 Unauthorized` / code `1003`: Missing/invalid JWT.
- `403 Forbidden` / code `1037`: Missing required role.
- `500 Internal Server Error` / code `1000`: Invalid date format or unhandled exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X GET "http://localhost:8080/api/v1/statistics/borrowing-activity?from=2026-04-01&to=2026-04-08&groupBy=day" \
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
      "time": "2026-04-01",
      "borrowCount": 12,
      "returnCount": 9
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
| limit | integer | false | Default `10`; if `<= 0`, service uses 10. |

### 5. Message Payload
- `None`

### 6. Header Parameters
- `Content-Type: application/json`
- `Authorization: None`

### 7. Authentication & Authorization Summary
- Authentication: `Public`
- Authorization: `No role restriction` (`permitAll`)

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Top borrowed books returned.

#### ❌ Error Responses
- `500 Internal Server Error` / code `1000`: Unhandled runtime/database exception.

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
      "title": "DDD",
      "borrowCount": 128
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
- Authorization: `SYSADMIN` only

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Active/new users stats returned.
- If `from > to`, service returns zeros with 200.

#### ❌ Error Responses
- `401 Unauthorized` / code `1003`: Missing/invalid JWT.
- `403 Forbidden` / code `1037`: Role is not `SYSADMIN`.
- `500 Internal Server Error` / code `1000`: Invalid date format or unhandled exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X GET "http://localhost:8080/api/v1/users/active-users?from=2026-04-01&to=2026-04-08" \
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

---

## GET /api/v1/statistics/system-totals

### 1. Title
- v1 Get System Totals

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
- Authorization: `SYSADMIN`, `LIBRARIAN`

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: System totals returned.

#### ❌ Error Responses
- `401 Unauthorized` / code `1003`: Missing/invalid JWT.
- `403 Forbidden` / code `1037`: Missing required role.
- `500 Internal Server Error` / code `1000`: Unhandled runtime/database exception.

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
