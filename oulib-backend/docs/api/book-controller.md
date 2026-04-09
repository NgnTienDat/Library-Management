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
| metadata.isbn | string | false | ISBN. Must be unique by business rule. |
| metadata.title | string | false | Book title. |
| metadata.publisher | string | false | Publisher. |
| metadata.numberOfPages | integer | false | Number of pages. |
| metadata.description | string | false | Description. |
| metadata.totalCopies | integer | true | Must be greater than 0. |
| metadata.categoryId | string | false | Category ID. Must exist. |
| metadata.copyBarcodes | array of string | true | Must match `totalCopies`, unique in request, and unique in DB. |
| metadata.authors | array of object (`AuthorRefRequest`) | false | Author references. |
| metadata.authors[].id | string | false | Existing author ID. |
| metadata.authors[].name | string | false | New author name when `id` is empty. |
| thumbnail | file | false | Image file for thumbnail. |

### 6. Header Parameters
- `Content-Type: multipart/form-data`
- `Authorization: Bearer <JWT_TOKEN>`

### 7. Authentication & Authorization Summary
- Authentication: `Requires JWT`
- Authorization: `LIBRARIAN` only (enforced at service layer)

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
  - Condition: Duplicate barcodes in request or existing barcode in DB.
- `404 Not Found`
  - Internal code: `1028`
  - Message: `Category not found`
  - Condition: `categoryId` does not exist.
- `404 Not Found`
  - Internal code: `1029`
  - Message: `Author not found`
  - Condition: Any provided author ID does not exist.
- `400 Bad Request`
  - Internal code: `2009`
  - Message: `Invalid author name`
  - Condition: Missing/blank author name when creating new author.
- `400 Bad Request`
  - Internal code: `2006`
  - Message: `Invalid image type`
  - Condition: `thumbnail` is not an image.
- `413 Payload Too Large`
  - Internal code: `413`
  - Message: `File size is too large!`
  - Condition: Uploaded file exceeds multipart limit.
- `500 Internal Server Error`
  - Internal code: `1000`
  - Message: `Uncategorized Error`
  - Condition: Any unhandled runtime exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X POST "http://localhost:8080/api/v1/books" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -F 'metadata={"isbn":"9786041234567","title":"DDD","totalCopies":2,"categoryId":"cat-001","copyBarcodes":["BC-0001","BC-0002"],"authors":[{"name":"Eric Evans"}]};type=application/json' \
  -F "thumbnail=@C:/tmp/book-cover.jpg"
```

#### Request JSON
```json
{
  "isbn": "9786041234567",
  "title": "DDD",
  "publisher": "Addison-Wesley",
  "numberOfPages": 560,
  "description": "Domain Driven Design",
  "totalCopies": 2,
  "categoryId": "cat-001",
  "copyBarcodes": ["BC-0001", "BC-0002"],
  "authors": [{ "name": "Eric Evans" }]
}
```

#### Success Response JSON
```json
{
  "code": 201,
  "message": "Created",
  "result": {
    "id": "book-001",
    "active": true,
    "title": "DDD",
    "isbn": "9786041234567",
    "publisher": "Addison-Wesley",
    "numberOfPages": 560,
    "description": "Domain Driven Design",
    "totalCopies": 2,
    "availableCopies": 2,
    "thumbnailUrl": "https://res.cloudinary.com/demo/image/upload/v1/oulib/thumbnails/book.jpg",
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
| keyword | string | false | Search across title, category name, and author name. |
| categoryId | string | false | Category filter. |
| authorIds | string | false | Comma-separated author IDs. |
| page | integer | false | Default `0`. |
| size | integer | false | Default `10`. |

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
  - Condition: Invalid paging values or any unhandled runtime exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X GET "http://localhost:8080/api/v1/books?keyword=ddd&page=0&size=10"
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
        "active": true,
        "title": "DDD",
        "isbn": "9786041234567",
        "publisher": "Addison-Wesley",
        "numberOfPages": 560,
        "description": "Domain Driven Design",
        "totalCopies": 2,
        "availableCopies": 1,
        "thumbnailUrl": "https://res.cloudinary.com/demo/image/upload/v1/oulib/thumbnails/book.jpg",
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
- `Authorization: None`

### 7. Authentication & Authorization Summary
- Authentication: `Public`
- Authorization: `No role restriction`

Note: `SecurityConfig` includes `/api/v1/books/**` in public GET endpoints.

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Book detail returned.

#### ❌ Error Responses
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
curl -X GET "http://localhost:8080/api/v1/books/book-001"
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
    "active": true,
    "title": "DDD",
    "isbn": "9786041234567",
    "publisher": "Addison-Wesley",
    "numberOfPages": 560,
    "description": "Domain Driven Design",
    "copoies": ["BC-0001", "BC-0002"],
    "availableCopies": 1,
    "thumbnailUrl": "https://res.cloudinary.com/demo/image/upload/v1/oulib/thumbnails/book.jpg",
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
- v1 Update Book

### 2. Endpoint
- `/api/v1/books/{id}`

### 3. Method
- `PATCH`

### 4. URL Parameters

| Name | Type | Required | Description |
|---|---|---|---|
| id | string | true | Book ID. |

### 5. Message Payload
Consumes `multipart/form-data`:

| Field | Type | Required | Description |
|---|---|---|---|
| metadata | object (`BookUpdateRequest`) | true | Partial book update payload. |
| metadata.title | string | false | Updated title. |
| metadata.publisher | string | false | Updated publisher. |
| metadata.numberOfPages | integer | false | Updated page count. |
| metadata.description | string | false | Updated description. |
| metadata.totalCopies | integer | false | Must be > 0 and not less than borrowed count. |
| metadata.categoryId | string | false | Updated category ID. |
| metadata.authors | array of object (`AuthorRefRequest`) | false | Replace authors if provided. |
| thumbnail | file | false | New thumbnail image. |

### 6. Header Parameters
- `Content-Type: multipart/form-data`
- `Authorization: Bearer <JWT_TOKEN>`

### 7. Authentication & Authorization Summary
- Authentication: `Requires JWT`
- Authorization: `LIBRARIAN` only (enforced at service layer)

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Book updated.

#### ❌ Error Responses
- `401 Unauthorized` / code `1003`: Missing/invalid JWT.
- `403 Forbidden` / code `1037`: Role is not `LIBRARIAN`.
- `404 Not Found` / code `1030`: Book not found.
- `400 Bad Request` / code `2008`: Invalid `totalCopies`.
- `404 Not Found` / code `1028`: Category not found.
- `404 Not Found` / code `1029`: Author not found.
- `400 Bad Request` / code `2009`: Invalid author name.
- `500 Internal Server Error` / code `1000`: Missing `metadata` part or other unhandled exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X PATCH "http://localhost:8080/api/v1/books/book-001" \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -F 'metadata={"title":"DDD Updated","totalCopies":3};type=application/json'
```

#### Request JSON
```json
{
  "title": "DDD Updated",
  "totalCopies": 3
}
```

#### Success Response JSON
```json
{
  "code": 200,
  "message": "Success",
  "result": {
    "id": "book-001",
    "active": true,
    "title": "DDD Updated",
    "isbn": "9786041234567",
    "publisher": "Addison-Wesley",
    "numberOfPages": 560,
    "description": "Domain Driven Design",
    "totalCopies": 3,
    "availableCopies": 2,
    "thumbnailUrl": "https://res.cloudinary.com/demo/image/upload/v1/oulib/thumbnails/book.jpg",
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
- Authorization: `LIBRARIAN` only

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Book deactivated (`active=false`).

#### ❌ Error Responses
- `401 Unauthorized` / code `1003`: Missing/invalid JWT.
- `403 Forbidden` / code `1037`: Role is not `LIBRARIAN`.
- `404 Not Found` / code `1030`: Book not found.
- `500 Internal Server Error` / code `1000`: Any unhandled exception.

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

---

## PATCH /api/v1/books/{id}/reactivate

### 1. Title
- v1 Reactivate Book

### 2. Endpoint
- `/api/v1/books/{id}/reactivate`

### 3. Method
- `PATCH`

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
- Authorization: `LIBRARIAN` only

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Book reactivated (`active=true`).

#### ❌ Error Responses
- `401 Unauthorized` / code `1003`: Missing/invalid JWT.
- `403 Forbidden` / code `1037`: Role is not `LIBRARIAN`.
- `404 Not Found` / code `1030`: Book not found.
- `500 Internal Server Error` / code `1000`: Any unhandled exception.

### 9. Sample Calls

#### cURL Example
```bash
curl -X PATCH "http://localhost:8080/api/v1/books/book-001/reactivate" \
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
  "result": "Book reactivated successfully"
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
