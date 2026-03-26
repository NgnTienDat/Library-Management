# BookController API Documentation

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

## POST /api/v1/books

### 1. Title
- v1 Create New Book

### 2. Endpoint
- `/api/v1/books`

### 3. Method
- `POST`

### 4. URL Parameters
- `None`

### 5. Message Payload
Consumes `multipart/form-data` with request parts:

| Field | Type | Required | Description |
|---|---|---|---|
| metadata | object (`BookCreationRequest`) | true | JSON metadata for book creation. |
| metadata.isbn | string | false | Book ISBN. No Bean Validation annotation in DTO. Must be unique by business rule. |
| metadata.title | string | false | Book title. No Bean Validation annotation in DTO. |
| metadata.publisher | string | false | Publisher name. |
| metadata.numberOfPages | integer | false | Number of pages. |
| metadata.description | string | false | Book description. |
| metadata.totalCopies | integer | true | Total physical copies. Must be greater than `0` by service rule. |
| metadata.categoryId | string | false | Category ID. Must exist if provided. |
| metadata.copyBarcodes | array of string | true | Barcode list for copies. Size must equal `totalCopies`; values must be unique and not already in DB. |
| metadata.authors | array of object (`AuthorRefRequest`) | false | Author references. |
| metadata.authors[].id | string | false | Existing author ID. Must exist if provided. |
| metadata.authors[].name | string | false | New author name when `id` is missing. Must not be blank. |
| thumbnail | file | false | Thumbnail image file. Must have content type starting with `image/`. |

### 6. Header Parameters
- `Content-Type: multipart/form-data`
- `Authorization: Bearer <JWT_TOKEN>`

### 7. Authentication & Authorization Summary
- Authentication: `Requires JWT`
- Authorization: `LIBRARIAN` only (enforced at service layer via `@PreAuthorize("hasRole('LIBRARIAN')")`)

### 8. Response Codes

#### ✅ Success Responses
- `201 Created`: Book created successfully.

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
  - Internal code: `1038`
  - Message: `The number of copy IDs does not match the total copies`
  - Condition: `copyBarcodes.size != totalCopies`.
- `400 Bad Request`
  - Internal code: `2008`
  - Message: `Total copies must be greater than 0`
  - Condition: `totalCopies <= 0`.
- `409 Conflict`
  - Internal code: `1027`
  - Message: `Book with this ISBN already exists`
  - Condition: ISBN already exists.
- `409 Conflict`
  - Internal code: `1039`
  - Message: `One or more barcodes already exist`
  - Condition: Duplicate barcode in request list or barcode already exists in DB.
- `404 Not Found`
  - Internal code: `1028`
  - Message: `Category not found`
  - Condition: `categoryId` does not exist.
- `404 Not Found`
  - Internal code: `1029`
  - Message: `Author not found`
  - Condition: Any `authors[].id` does not exist.
- `400 Bad Request`
  - Internal code: `2009`
  - Message: `Invalid author name`
  - Condition: New author item has missing/blank `name`.
- `400 Bad Request`
  - Internal code: `2006`
  - Message: `Invalid image type`
  - Condition: Uploaded `thumbnail` is not an image content type.
- `413 Payload Too Large`
  - Internal code: `413`
  - Message: `File size is too large!`
  - Condition: Multipart file exceeds configured limit.
- `500 Internal Server Error`
  - Internal code: `1000`
  - Message: `Uncategorized Error`
  - Condition: Any unhandled runtime exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X POST "http://localhost:8080/api/v1/books" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -F 'metadata={
    "isbn":"9786041234567",
    "title":"Domain-Driven Design",
    "publisher":"Addison-Wesley",
    "numberOfPages":560,
    "description":"Tactical and strategic design patterns",
    "totalCopies":2,
    "categoryId":"cat-001",
    "copyBarcodes":["BC-0001","BC-0002"],
    "authors":[{"name":"Eric Evans"}]
  };type=application/json' \
  -F "thumbnail=@C:/tmp/book-cover.jpg"
```

#### Request JSON
```json
{
  "isbn": "9786041234567",
  "title": "Domain-Driven Design",
  "publisher": "Addison-Wesley",
  "numberOfPages": 560,
  "description": "Tactical and strategic design patterns",
  "totalCopies": 2,
  "categoryId": "cat-001",
  "copyBarcodes": ["BC-0001", "BC-0002"],
  "authors": [
    {
      "name": "Eric Evans"
    }
  ]
}
```

#### Success Response JSON
```json
{
  "code": 201,
  "message": "Created",
  "result": {
    "id": "book-001",
    "title": "Domain-Driven Design",
    "isbn": "9786041234567",
    "publisher": "Addison-Wesley",
    "numberOfPages": 560,
    "description": "Tactical and strategic design patterns",
    "totalCopies": 2,
    "availableCopies": 2,
    "thumbnailUrl": "https://res.cloudinary.com/demo/image/upload/v1/oulib/thumbnails/book-cover.jpg",
    "categoryName": "Software Architecture",
    "authorNames": ["Eric Evans"]
  }
}
```

#### Error Response JSON
```json
{
  "code": 1039,
  "message": "One or more barcodes already exist",
  "result": null
}
```

---

## GET /api/v1/books

### 1. Title
- v1 Get Books With Filters And Pagination

### 2. Endpoint
- `/api/v1/books`

### 3. Method
- `GET`

### 4. URL Parameters

| Name | Type | Required | Description |
|---|---|---|---|
| keyword | string | false | Keyword search across title, category name, and author name. |
| categoryId | string | false | Filter by category ID. |
| authorIds | string | false | Comma-separated author IDs; parsed into list (e.g., `a1,a2,a3`). |
| page | integer | false | Page index, default `0`. |
| size | integer | false | Page size, default `10`. |

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
- `200 OK`: Book list returned.

#### ❌ Error Responses
- `500 Internal Server Error`
  - Internal code: `1000`
  - Message: `Uncategorized Error`
  - Condition: Invalid pagination values (for example negative page/size) or any unhandled runtime exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X GET "http://localhost:8080/api/v1/books?keyword=design&categoryId=cat-001&authorIds=a1,a2&page=0&size=10"
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
        "id": "book-001",
        "title": "Domain-Driven Design",
        "isbn": "9786041234567",
        "publisher": "Addison-Wesley",
        "numberOfPages": 560,
        "description": "Tactical and strategic design patterns",
        "totalCopies": 2,
        "availableCopies": 1,
        "thumbnailUrl": "https://res.cloudinary.com/demo/image/upload/v1/oulib/thumbnails/book-cover.jpg",
        "categoryName": "Software Architecture",
        "authorNames": ["Eric Evans"]
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
  "code": 1000,
  "message": "Uncategorized Error",
  "result": null
}
```

---

## GET /api/v1/books/{id}

### 1. Title
- v1 Get Book Detail By ID

### 2. Endpoint
- `/api/v1/books/{id}`

### 3. Method
- `GET`

### 4. URL Parameters

| Name | Type | Required | Description |
|---|---|---|---|
| id | string | true | Book ID. |

### 5. Message Payload
- `None`

### 6. Header Parameters
- `Content-Type: application/json`
- `Authorization: Bearer <JWT_TOKEN>`

### 7. Authentication & Authorization Summary
- Authentication: `Requires JWT`
- Authorization: `Any authenticated role` (`USER`, `LIBRARIAN`, `SYSADMIN`)

Note: `/api/v1/books/{id}` is not listed in public GET matchers, so authentication is required even though `/api/v1/books` list is public.

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Book details returned.

#### ❌ Error Responses
- `401 Unauthorized`
  - Internal code: `1003`
  - Message: `Unauthenticated`
  - Condition: Missing/invalid/expired/blacklisted JWT.
- `404 Not Found`
  - Internal code: `1030`
  - Message: `Book not found`
  - Condition: Book ID does not exist.
- `500 Internal Server Error`
  - Internal code: `1000`
  - Message: `Uncategorized Error`
  - Condition: Any unhandled runtime exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X GET "http://localhost:8080/api/v1/books/book-001" \
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
    "id": "book-001",
    "title": "Domain-Driven Design",
    "isbn": "9786041234567",
    "publisher": "Addison-Wesley",
    "numberOfPages": 560,
    "description": "Tactical and strategic design patterns",
    "totalCopies": 2,
    "availableCopies": 1,
    "thumbnailUrl": "https://res.cloudinary.com/demo/image/upload/v1/oulib/thumbnails/book-cover.jpg",
    "categoryName": "Software Architecture",
    "authorNames": ["Eric Evans"]
  }
}
```

#### Error Response JSON
```json
{
  "code": 1030,
  "message": "Book not found",
  "result": null
}
```

---

## PATCH /api/v1/books/{id}

### 1. Title
- v1 Update Book Information

### 2. Endpoint
- `/api/v1/books/{id}`

### 3. Method
- `PATCH`

### 4. URL Parameters

| Name | Type | Required | Description |
|---|---|---|---|
| id | string | true | Book ID. |

### 5. Message Payload
Consumes `multipart/form-data` with request parts:

| Field | Type | Required | Description |
|---|---|---|---|
| metadata | object (`BookUpdateRequest`) | true | JSON metadata for update. |
| metadata.title | string | false | New title. |
| metadata.publisher | string | false | New publisher. |
| metadata.numberOfPages | integer | false | New page count. |
| metadata.description | string | false | New description. |
| metadata.totalCopies | integer | false | New total copies. Must be `> 0` and not less than currently borrowed copies. |
| metadata.categoryId | string | false | New category ID. Must exist if provided. |
| metadata.authors | array of object (`AuthorRefRequest`) | false | Replaces current author list when provided. |
| metadata.authors[].id | string | false | Existing author ID. Must exist if provided. |
| metadata.authors[].name | string | false | New author name when `id` is missing. Must not be blank. |
| thumbnail | file | false | New thumbnail image. |

Behavior detail for `thumbnail` update:
- Invalid upload/image handling is logged and suppressed in service; API still returns success response with current book state.

### 6. Header Parameters
- `Content-Type: multipart/form-data`
- `Authorization: Bearer <JWT_TOKEN>`

### 7. Authentication & Authorization Summary
- Authentication: `Requires JWT`
- Authorization: `LIBRARIAN` only (enforced at service layer via `@PreAuthorize("hasRole('LIBRARIAN')")`)

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Book update processed.

#### ❌ Error Responses
- `401 Unauthorized`
  - Internal code: `1003`
  - Message: `Unauthenticated`
  - Condition: Missing/invalid/expired/blacklisted JWT.
- `403 Forbidden`
  - Internal code: `1037`
  - Message: `You do not have permission to perform this action`
  - Condition: Authenticated user is not `LIBRARIAN`.
- `404 Not Found`
  - Internal code: `1030`
  - Message: `Book not found`
  - Condition: Book ID does not exist.
- `400 Bad Request`
  - Internal code: `2008`
  - Message: `Total copies must be greater than 0`
  - Condition: `totalCopies <= 0` or requested total is less than currently borrowed copies.
- `404 Not Found`
  - Internal code: `1028`
  - Message: `Category not found`
  - Condition: `categoryId` does not exist.
- `404 Not Found`
  - Internal code: `1029`
  - Message: `Author not found`
  - Condition: Any `authors[].id` does not exist.
- `400 Bad Request`
  - Internal code: `2009`
  - Message: `Invalid author name`
  - Condition: New author item has missing/blank `name`.
- `500 Internal Server Error`
  - Internal code: `1000`
  - Message: `Uncategorized Error`
  - Condition: Missing required multipart part `metadata` or any unhandled runtime exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X PATCH "http://localhost:8080/api/v1/books/book-001" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -F 'metadata={
    "title":"Domain-Driven Design (Updated)",
    "totalCopies":3,
    "categoryId":"cat-001",
    "authors":[{"id":"author-001"}]
  };type=application/json' \
  -F "thumbnail=@C:/tmp/new-cover.jpg"
```

#### Request JSON
```json
{
  "title": "Domain-Driven Design (Updated)",
  "publisher": "Addison-Wesley",
  "numberOfPages": 560,
  "description": "Updated description",
  "totalCopies": 3,
  "categoryId": "cat-001",
  "authors": [
    {
      "id": "author-001"
    }
  ]
}
```

#### Success Response JSON
```json
{
  "code": 200,
  "message": "Success",
  "result": {
    "id": "book-001",
    "title": "Domain-Driven Design (Updated)",
    "isbn": "9786041234567",
    "publisher": "Addison-Wesley",
    "numberOfPages": 560,
    "description": "Updated description",
    "totalCopies": 3,
    "availableCopies": 2,
    "thumbnailUrl": "https://res.cloudinary.com/demo/image/upload/v1/oulib/thumbnails/new-cover.jpg",
    "categoryName": "Software Architecture",
    "authorNames": ["Eric Evans"]
  }
}
```

#### Error Response JSON
```json
{
  "code": 2008,
  "message": "Total copies must be greater than 0",
  "result": null
}
```

---

## DELETE /api/v1/books/{id}

### 1. Title
- v1 Deactivate Book

### 2. Endpoint
- `/api/v1/books/{id}`

### 3. Method
- `DELETE`

### 4. URL Parameters

| Name | Type | Required | Description |
|---|---|---|---|
| id | string | true | Book ID. |

### 5. Message Payload
- `None`

### 6. Header Parameters
- `Content-Type: application/json`
- `Authorization: Bearer <JWT_TOKEN>`

### 7. Authentication & Authorization Summary
- Authentication: `Requires JWT`
- Authorization: `LIBRARIAN` only (enforced at service layer via `@PreAuthorize("hasRole('LIBRARIAN')")`)

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Book was soft-deleted by setting `active = false`.

#### ❌ Error Responses
- `401 Unauthorized`
  - Internal code: `1003`
  - Message: `Unauthenticated`
  - Condition: Missing/invalid/expired/blacklisted JWT.
- `403 Forbidden`
  - Internal code: `1037`
  - Message: `You do not have permission to perform this action`
  - Condition: Authenticated user is not `LIBRARIAN`.
- `404 Not Found`
  - Internal code: `1030`
  - Message: `Book not found`
  - Condition: Book ID does not exist.
- `500 Internal Server Error`
  - Internal code: `1000`
  - Message: `Uncategorized Error`
  - Condition: Any unhandled runtime exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X DELETE "http://localhost:8080/api/v1/books/book-001" \
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
  "result": "Book deactivated successfully"
}
```

#### Error Response JSON
```json
{
  "code": 1030,
  "message": "Book not found",
  "result": null
}
```
