# ADR-002: Use MySQL as Primary Database

## Status
Accepted

## Date
2026-03-28

## Context
Hệ thống quản lý thư viện lưu trữ dữ liệu có quan hệ chặt chẽ giữa các thực thể:
- User
- Book
- Category
- Borrow/Return

Các nghiệp vụ cốt lõi như mượn/trả sách yêu cầu:
- Tính toàn vẹn dữ liệu cao (ACID)
- Tránh sai lệch số lượng sách (race condition, dirty write)

Ngoài ra, hệ thống được phát triển trong thời gian ngắn (6 tuần), với đội ngũ nhỏ (1 Backend Developer), và traffic dự kiến thấp.

## Decision Drivers
- Đảm bảo ACID transaction cho nghiệp vụ mượn/trả
- Phù hợp với mô hình dữ liệu quan hệ (relational model)
- Tối ưu tốc độ phát triển (time-to-market)
- Tận dụng kinh nghiệm sẵn có của team
- Giảm thiểu rủi ro kỹ thuật trong thời gian ngắn

## Considered Options

### Option 1: MySQL
**Description**: Hệ quản trị cơ sở dữ liệu quan hệ (RDBMS) phổ biến, mã nguồn mở, được sử dụng rộng rãi trong các hệ thống CRUD.

**Pros**:
- Team đã quen thuộc → không mất thời gian học
- Hỗ trợ ACID transaction tốt (InnoDB)
- Dễ thiết lập, vận hành và tích hợp với Spring ecosystem
- Hiệu năng tốt cho workload CRUD và query đơn giản
- Cộng đồng lớn, dễ tìm tài liệu và troubleshooting

**Cons**:
- Hạn chế hơn PostgreSQL trong các query phức tạp
- Hỗ trợ JSON và full-text search không mạnh bằng PostgreSQL

---

### Option 2: PostgreSQL
**Description**: RDBMS mạnh mẽ với nhiều tính năng nâng cao (CTE, window function, JSONB, full-text search).

**Pros**:
- Hỗ trợ query phức tạp tốt hơn
- Full-text search mạnh
- JSONB linh hoạt

**Cons**:
- Learning curve cao hơn với team hiện tại
- Tăng thời gian development và debugging
- Nhiều tính năng nâng cao không cần thiết cho scope hệ thống

---

### Option 3: MongoDB
**Description**: NoSQL document database, schema linh hoạt.

**Pros**:
- Linh hoạt về schema
- Dễ mapping object

**Cons**:
- Không phù hợp với dữ liệu quan hệ (borrow liên kết nhiều entity)
- Khó đảm bảo ACID transaction như RDBMS
- Tăng độ phức tạp cho nghiệp vụ cần consistency cao

---

## Decision
We decided on **MySQL** as the primary database because it provides the best balance between **simplicity, reliability, and development speed** for the current system.

- MySQL fully satisfies the requirement for ACID transactions, which is critical for borrow/return operations.
- The data model is highly relational, making an RDBMS the most appropriate choice.
- The development team is already familiar with MySQL, eliminating learning overhead and reducing implementation risk.
- Advanced features of PostgreSQL (e.g., complex queries, full-text search) are not required for the current scope, and adopting them would introduce unnecessary complexity.

Therefore, MySQL is the most pragmatic and low-risk choice to ensure timely delivery of the system.

## Consequences

### Positive
- Rút ngắn thời gian development do không cần học công nghệ mới
- Đảm bảo tính nhất quán và toàn vẹn dữ liệu cho nghiệp vụ quan trọng
- Dễ triển khai và vận hành trong môi trường đơn giản (Docker/VPS)
- Giảm rủi ro lỗi kỹ thuật trong quá trình phát triển

### Negative
- Khả năng xử lý search nâng cao bị hạn chế
- Nếu hệ thống mở rộng trong tương lai, có thể cần bổ sung giải pháp khác (e.g., search engine)

### Neutral
- Có thể migrate sang PostgreSQL trong tương lai nếu yêu cầu hệ thống thay đổi (đổi cấu hình DB, check native query syntax)
- Cần thiết kế index hợp lý để đảm bảo hiệu năng query