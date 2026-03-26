Your task is to read source code and generate a complete, production-ready API documentation in Markdown (.md) format.
- Location document: oulib-backend/docs/api
The system is a Library Management System with 3 roles:
- USER
- LIBRARIAN
- SYSADMIN

All API responses follow a standard wrapper:

ApiResponse:
{
  "code": <int>,
  "message": "<string>",
  "result": <object | null>
}

---

## 🛑 STRICT WORKING RULES (MANDATORY)

1. ONLY document ONE RestController per response.
2. After finishing, STOP and wait for user approval.
3. ONLY continue when user explicitly says:
   "OK, continue with <ControllerName>"
4. DO NOT document any other controller.

---

## 🎯 SCOPE

- Target Controller: AuthenticationController
- Do NOT include any other controllers

---

## 🔍 DEEP ANALYSIS REQUIREMENTS (CRITICAL)

You MUST perform deep code analysis before writing documentation:

1. Read the Controller carefully
2. Trace ALL service methods called inside the Controller
3. Read and analyze:
   - Request DTOs
   - Response DTOs
   - Related Entities (if needed)
4. Analyze ALL exceptions:
   - Especially AppException thrown from Service layer
5. Extract REAL response structure (DO NOT guess)

---

## 🔐 AUTHENTICATION & AUTHORIZATION

### Authentication (JWT)

- Read SecurityConfig to determine:
  - Public endpoints (no token required)
  - Protected endpoints (require JWT)

- Explicitly state for each API:
  - Public API (no authentication required)
  - OR Requires JWT (Bearer Token)

### Authorization (Roles)

- Extract from annotations:
  @PreAuthorize("hasRole('ROLE_NAME')")

- Clearly list allowed roles:
  - USER
  - LIBRARIAN
  - SYSADMIN

---

## 📚 DOCUMENTATION STRUCTURE (MANDATORY)

For EACH API endpoint, use EXACTLY the following structure:

---

### 1. Title
- Short, clear description
- Include API version if available (e.g., v1)

### 2. Endpoint
- Full URL path

### 3. Method
- HTTP method (GET, POST, PUT, DELETE, ...)

### 4. URL Parameters
For each parameter:
- Name
- Type
- Required (true/false)
- Description

Include:
- Path variables
- Query parameters

If none → write: `None`

---

### 5. Message Payload
- Based strictly on Request DTO

For each field:
- Field name
- Type
- Required (based on validation annotations like @NotNull, @NotBlank,...)
- Description

If no body → write: `None`

---

### 6. Header Parameters

- Content-Type: application/json

- Authorization:
  - Bearer <JWT_TOKEN> (if required)
  - None (if public)

---

### 7. Authentication & Authorization Summary

- Authentication:
  - Public / Requires JWT

- Authorization:
  - List roles allowed (if any)
  - If no @PreAuthorize → assume authenticated user (if protected endpoint)

---

### 8. Response Codes

You MUST include:

#### ✅ Success Responses
- HTTP Status
- Description

#### ❌ Error Responses

For each error:
- HTTP Status Code
- Internal code (from ApiResponse.code if identifiable)
- Message
- Condition (when it happens)

You MUST derive errors from:
- Service logic
- AppException
- Validation constraints

Typical examples (but DO NOT assume blindly):
- 400 Bad Request
- 401 Unauthorized
- 403 Forbidden
- 404 Not Found
- 409 Conflict
- 500 Internal Server Error

---

### 9. Sample Calls

#### cURL Example

#### Request JSON (if applicable)

#### Success Response JSON
- MUST follow ApiResponse structure

#### Error Response JSON
- At least one realistic example based on actual exception

---

## ❌ STRICT RESTRICTIONS

- DO NOT assume DTO fields — must match actual code
- DO NOT skip error cases
- DO NOT generate vague descriptions
- DO NOT include explanations outside Markdown
- DO NOT document multiple controllers
- DO NOT hallucinate response structures

---

## ✅ OUTPUT REQUIREMENTS

- Output format: Markdown (.md)
- Clean, structured, production-ready
- Use headings, bullet points, code blocks
- No extra commentary outside documentation

---

## 🚀 START

Generate API documentation for AuthenticationController ONLY.

Wait for user review after completion.