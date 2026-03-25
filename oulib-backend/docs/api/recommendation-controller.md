# RecommendationController API Documentation

Base path: `/api/v1/recommendations`

Standard response wrapper used by all APIs:

```json
{
  "code": 200,
  "message": "Success",
  "result": {}
}
```

---

## GET /api/v1/recommendations/popular

### 1. Title
- v1 Get Popular Book Recommendations

### 2. Endpoint
- `/api/v1/recommendations/popular`

### 3. Method
- `GET`

### 4. URL Parameters
- `None`

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
- `200 OK`: Returns top popular active books (up to 10), ranked by borrow frequency.

#### ❌ Error Responses
- `500 Internal Server Error`
  - Internal code: `1000`
  - Message: `Uncategorized Error`
  - Condition: Any unhandled runtime exception while loading ranking/book data.

### 9. Sample Calls

#### cURL Example
```bash
curl -X GET "http://localhost:8080/api/v1/recommendations/popular"
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
      "id": "book-001",
      "title": "Domain-Driven Design",
      "isbn": "9786041234567",
      "publisher": "Addison-Wesley",
      "numberOfPages": 560,
      "description": "Tactical and strategic design patterns",
      "totalCopies": 5,
      "availableCopies": 2,
      "thumbnailUrl": "https://res.cloudinary.com/demo/image/upload/v1/oulib/thumbnails/book-001.jpg",
      "categoryName": "Software Architecture",
      "authorNames": ["Eric Evans"]
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

## GET /api/v1/recommendations/trending

### 1. Title
- v1 Get Trending Book Recommendations

### 2. Endpoint
- `/api/v1/recommendations/trending`

### 3. Method
- `GET`

### 4. URL Parameters
- `None`

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
- `200 OK`: Returns trending active books (up to 10), ranked by borrow frequency since `LocalDate.now().minusDays(1)`.

#### ❌ Error Responses
- `500 Internal Server Error`
  - Internal code: `1000`
  - Message: `Uncategorized Error`
  - Condition: Any unhandled runtime exception while loading ranking/book data.

### 9. Sample Calls

#### cURL Example
```bash
curl -X GET "http://localhost:8080/api/v1/recommendations/trending"
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
      "id": "book-020",
      "title": "Clean Architecture",
      "isbn": "9780134494166",
      "publisher": "Prentice Hall",
      "numberOfPages": 432,
      "description": "A craftsman's guide to software structure and design",
      "totalCopies": 4,
      "availableCopies": 1,
      "thumbnailUrl": "https://res.cloudinary.com/demo/image/upload/v1/oulib/thumbnails/book-020.jpg",
      "categoryName": "Software Engineering",
      "authorNames": ["Robert C. Martin"]
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

## GET /api/v1/recommendations/personalized

### 1. Title
- v1 Get Personalized Book Recommendations

### 2. Endpoint
- `/api/v1/recommendations/personalized`

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

Personalization behavior from service:
- Uses authenticated user email (`jwt.subject`) to detect borrow history.
- If no history, fallback to popular recommendations.
- Primary signal: top borrowed category.
- If category recommendations are insufficient, fills remaining slots with popular books excluding already borrowed items.
- Maximum result size is 10.

### 8. Response Codes

#### ✅ Success Responses
- `200 OK`: Returns personalized/fallback recommendations.

#### ❌ Error Responses
- `401 Unauthorized`
  - Internal code: `1003`
  - Message: `Unauthenticated`
  - Condition: Missing/invalid/expired/blacklisted JWT, or empty JWT subject in service-level check.
- `500 Internal Server Error`
  - Internal code: `1000`
  - Message: `Uncategorized Error`
  - Condition: Any unhandled runtime exception while loading recommendation data.

### 9. Sample Calls

#### cURL Example
```bash
curl -X GET "http://localhost:8080/api/v1/recommendations/personalized" \
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
      "id": "book-015",
      "title": "Design Patterns",
      "isbn": "9780201633610",
      "publisher": "Addison-Wesley",
      "numberOfPages": 395,
      "description": "Elements of Reusable Object-Oriented Software",
      "totalCopies": 3,
      "availableCopies": 1,
      "thumbnailUrl": "https://res.cloudinary.com/demo/image/upload/v1/oulib/thumbnails/book-015.jpg",
      "categoryName": "Software Architecture",
      "authorNames": ["Erich Gamma", "Richard Helm", "Ralph Johnson", "John Vlissides"]
    }
  ]
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
