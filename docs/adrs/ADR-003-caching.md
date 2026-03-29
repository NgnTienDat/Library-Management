# ADR-003: Use JWT with Redis Token Blocklist for Authentication

## Status
Accepted

## Date
2026-03-28

## Context
Hệ thống cần cung cấp cơ chế xác thực (authentication) cho nhiều loại người dùng (Độc giả, Thủ thư, Admin). API được thiết kế theo hướng RESTful, phục vụ frontend web/mobile, do đó cần một cơ chế xác thực **stateless** để dễ dàng mở rộng và tích hợp.

Tuy nhiên, hệ thống cũng yêu cầu:
- Có thể **đăng xuất (logout) ngay lập tức**
- Có thể **vô hiệu hóa token** khi người dùng đổi mật khẩu hoặc bị khóa tài khoản

Điều này tạo ra mâu thuẫn giữa:
- Stateless authentication (JWT)
- Khả năng revoke token (thường cần state)

## Decision Drivers
- Cần cơ chế auth stateless để tối ưu hiệu năng và khả năng mở rộng
- Phải hỗ trợ logout thực sự (token invalidation)
- Tốc độ xử lý request phải nhanh (low latency)
- Giảm tải cho database chính (MySQL)

## Considered Options

### Option 1: Session-based Authentication
**Description**: Lưu session trên server (DB hoặc Redis), client giữ session ID

**Pros**:
- Dễ dàng quản lý logout và revoke session
- Đơn giản về mặt logic

**Cons**:
- Server phải giữ state → khó scale
- Không phù hợp với RESTful API và mobile client
- Tăng load lên database hoặc session store

---

### Option 2: Pure JWT (Stateless)
**Description**: Sử dụng JWT hoàn toàn stateless, không lưu trữ gì phía server

**Pros**:
- Hiệu năng cao (không cần truy vấn DB khi verify)
- Phù hợp với kiến trúc RESTful

**Cons**:
- Không thể logout thực sự (token vẫn hợp lệ đến khi hết hạn)
- Không thể revoke token khi có sự cố bảo mật

---

### Option 3: JWT + Redis Token Blocklist
**Description**: Sử dụng JWT cho authentication. Khi logout hoặc revoke, lưu identifier của token (JTI) vào Redis với TTL tương ứng với thời gian sống còn lại của token.

**Pros**:
- Giữ được tính stateless của JWT cho hầu hết request
- Hỗ trợ logout và revoke token một cách hiệu quả
- Redis truy xuất nhanh (in-memory), không ảnh hưởng đáng kể đến performance
- Giảm tải cho database chính

**Cons**:
- Thêm một dependency (Redis) vào hệ thống
- Cần thêm logic kiểm tra token

---

## Decision
We decided on **JWT + Redis Token Blocklist** because it provides the best balance between **performance, scalability, and security**.

- JWT đảm bảo hệ thống giữ được tính stateless và phản hồi nhanh.
- Redis được sử dụng như một **token blacklist store** để xử lý các trường hợp cần revoke token ngay lập tức.
- Giải pháp này tránh được nhược điểm lớn nhất của pure JWT (không logout được) mà vẫn không làm hệ thống trở nên stateful như session-based authentication.

Đây là một giải pháp thực tế, широко được áp dụng trong các hệ thống backend hiện đại.

## Consequences

### Positive
- Hỗ trợ logout và revoke token một cách an toàn
- Hiệu năng cao nhờ Redis (in-memory)
- Giảm tải cho database chính
- Phù hợp với kiến trúc RESTful và dễ tích hợp frontend/mobile

### Negative
- Cần triển khai và vận hành thêm Redis
- Tăng nhẹ độ phức tạp trong middleware (check blacklist)

### Neutral
- Redis có thể được tái sử dụng cho nhiều mục đích khác trong tương lai, ví dụ:
  - Caching dữ liệu (book list, search results)
  - Rate limiting (giới hạn request)
  - Lưu session tạm thời hoặc OTP
  - Queue nhẹ cho background jobs