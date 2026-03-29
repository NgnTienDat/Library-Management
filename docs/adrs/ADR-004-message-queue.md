# ADR-004: Use RabbitMQ for Asynchronous Processing

## Status
Accepted

## Date
2026-03-28

## Context
Hệ thống có một số tác vụ không cần xử lý đồng bộ trong luồng HTTP request, bao gồm:
- Gửi email nhắc hạn trả sách
- Ghi log hoạt động (audit log)

Nếu thực hiện các tác vụ này trực tiếp trong request:
- Thời gian phản hồi API sẽ tăng (high latency)
- Trải nghiệm người dùng bị ảnh hưởng
- Dễ gây timeout nếu có lỗi I/O (SMTP, disk, network)

Do đó, cần một cơ chế xử lý bất đồng bộ để tách các tác vụ này khỏi luồng chính.

## Decision Drivers
- Giảm thời gian phản hồi API (low latency)
- Đảm bảo độ tin cậy (không mất email/log khi hệ thống gặp sự cố)
- Tách biệt rõ ràng giữa request processing và background processing
- Phù hợp với quy mô hệ thống nhỏ, không over-engineering

## Considered Options

### Option 1: In-memory Queue
**Description**: Sử dụng queue trong bộ nhớ (e.g., BlockingQueue)

**Pros**:
- Dễ triển khai
- Không cần thêm service

**Cons**:
- Mất dữ liệu khi ứng dụng restart/crash
- Không hỗ trợ retry, persistence
- Khó scale worker độc lập

---

### Option 2: Apache Kafka
**Description**: Distributed event streaming platform

**Pros**:
- Throughput cao
- Lưu trữ event lâu dài
- Phù hợp hệ thống lớn

**Cons**:
- Setup và vận hành phức tạp
- Overkill cho hệ thống nhỏ
- Tốn tài nguyên (RAM, CPU)

---

### Option 3: RabbitMQ
**Description**: Message broker sử dụng AMQP, phù hợp mô hình task queue (producer–consumer)

**Pros**:
- Hỗ trợ message persistence → không mất data khi crash
- Có cơ chế retry và acknowledgment
- Phù hợp với mô hình xử lý background jobs
- Dễ cấu hình và nhẹ hơn Kafka
- Tách biệt rõ ràng producer (API) và consumer (worker)

**Cons**:
- Cần triển khai và quản lý thêm một service
- Tăng độ phức tạp hệ thống ở mức vừa phải

---

## Decision
We decided on **RabbitMQ** because it provides a **reliable and scalable asynchronous processing mechanism** while still keeping the system relatively simple.

- RabbitMQ cho phép API trả response ngay lập tức sau khi publish message, giúp giảm latency đáng kể.
- Các tác vụ như gửi email và logging được xử lý bởi worker riêng, tránh block request.
- Cơ chế persistence và acknowledgment đảm bảo message không bị mất ngay cả khi hệ thống gặp sự cố.

So với Kafka, RabbitMQ đơn giản hơn nhiều và phù hợp với quy mô hệ thống hiện tại. So với in-memory queue, RabbitMQ đảm bảo độ tin cậy cao hơn.

## Consequences

### Positive
- Cải thiện đáng kể thời gian phản hồi API
- Đảm bảo độ tin cậy cho các tác vụ bất đồng bộ
- Tách biệt rõ ràng giữa business logic và background processing
- Có thể scale worker độc lập nếu workload tăng

### Negative
- Tăng độ phức tạp hạ tầng (cần RabbitMQ service)
- Cần thêm logic xử lý message (producer/consumer, retry)

### Neutral
- Mở ra khả năng áp dụng event-driven pattern trong tương lai
- RabbitMQ có thể được tái sử dụng cho các use case khác như:
  - Notification system
  - Event logging
  - Integration với các service bên ngoài