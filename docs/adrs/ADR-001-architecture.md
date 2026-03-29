# ADR-001: Use Monolithic Architecture with Asynchronous Worker

## Status
Accepted

## Date
2026-03-28

## Context
Hệ thống quản lý thư viện cần được phát triển và triển khai trong thời gian ngắn (2 tuần phân tích, 6 tuần phát triển). Đội ngũ chỉ có 1 Backend Developer. Hệ thống phục vụ nội bộ, traffic thấp, không yêu cầu scale lớn.

Ngoài ra, hệ thống có một số tác vụ không cần xử lý đồng bộ trong request như:
- Gửi email nhắc hạn
- Ghi log
- Xử lý background jobs (Hiện tại chỉ có 1 scheduler và đang xử lý ở api instance)

## Decision Drivers
- Thời gian phát triển ngắn (6 tuần)
- Nhân sự hạn chế (1 Backend)
- Traffic thấp, hệ thống đơn giản
- Cần tối ưu thời gian triển khai nhanh (MVP)
- Cần xử lý bất đồng bộ để giảm latency

## Considered Options

### Option 1: Monolithic Architecture
**Description**: Tất cả module nằm trong một codebase, chạy trên một process

**Pros**:
- Dễ phát triển, debug, test
- Triển khai đơn giản
- Không có network latency giữa các module

**Cons**:
- Khó scale từng phần
- Dễ trở nên lộn xộn nếu không tổ chức tốt
- Không tối ưu cho các tác vụ async

---

### Option 2: Microservices Architecture
**Description**: Tách thành nhiều service độc lập

**Pros**:
- Scale độc lập
- Rõ ràng domain

**Cons**:
- Overhead lớn
- Phức tạp CI/CD và infra
- Khó quản lý với team size nhỏ (<5 developer)
- Không phù hợp với scope nhỏ

---

### Option 3: Monolithic + Asynchronous Worker
**Description**: Sử dụng một ứng dụng Monolith làm core xử lý request chính, kết hợp với một worker riêng (background process) để xử lý các tác vụ bất đồng bộ thông qua message queue.

**Pros**:
- Giữ được sự đơn giản của monolith
- Giảm latency cho API (không block request)
- Tách biệt rõ ràng giữa request processing và background processing
- Dễ mở rộng worker nếu cần

**Cons**:
- Phải quản lý thêm worker process
- Tăng độ phức tạp nhẹ so với monolith thuần
- Cần thêm message queue (RabbitMQ)

---

## Decision
We decided on **Option 3 (Monolith + Asynchronous Worker)** because nó cân bằng tốt nhất giữa **độ đơn giản và khả năng mở rộng** trong bối cảnh hệ thống.

- Monolith được sử dụng làm core để đảm bảo tốc độ phát triển nhanh và dễ maintain.
- Asynchronous Worker được bổ sung để xử lý các tác vụ không cần đồng bộ (email, logging), giúp giảm thời gian phản hồi API và cải thiện trải nghiệm người dùng.

Giải pháp này tránh được over-engineering của microservices, đồng thời vẫn đảm bảo hệ thống có khả năng xử lý bất đồng bộ một cách hiệu quả.

## Consequences

### Positive
- Phát triển nhanh như monolith
- API response nhanh hơn nhờ async processing
- Tách biệt rõ ràng giữa business logic và background jobs
- Có thể scale worker độc lập nếu cần

### Negative
- Phải thiết kế message queue và worker
- Tăng nhẹ độ phức tạp hệ thống
- Cần monitoring cho worker

