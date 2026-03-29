# ADR-005: Use MySQL-Based Search Instead of Elasticsearch

## Status
Accepted

## Date
2026-03-28

## Context
Hệ thống cần cung cấp chức năng tìm kiếm sách theo các tiêu chí:
- Tên sách (title)
- Tác giả (author)
- Thể loại (category)

Dữ liệu dự kiến ở mức nhỏ đến trung bình (hàng chục nghìn bản ghi), và yêu cầu tìm kiếm chủ yếu là:
- Filter theo điều kiện
- Tìm kiếm keyword cơ bản

Trong khi đó, thời gian phát triển hệ thống chỉ có 6 tuần và không có ngân sách cho hạ tầng phức tạp.

## Decision Drivers
- Tránh over-engineering cho hệ thống quy mô nhỏ
- Tối ưu thời gian phát triển (time-to-market)
- Không phát sinh thêm chi phí hạ tầng
- Đảm bảo đủ đáp ứng nhu cầu tìm kiếm hiện tại

## Considered Options

### Option 1: Elasticsearch
**Description**: Distributed search engine chuyên dụng cho full-text search và analytics

**Pros**:
- Full-text search mạnh (stemming, ranking, typo tolerance)
- Hiệu năng cao với dataset lớn
- Hỗ trợ search nâng cao (relevance scoring, fuzzy search)

**Cons**:
- Cần thêm hạ tầng riêng (cluster, memory-intensive)
- Tốn thời gian setup và vận hành
- Phải đồng bộ dữ liệu từ database chính (MySQL → Elasticsearch)
- Overkill với hệ thống nhỏ, traffic thấp

---

### Option 2: MySQL LIKE / FULLTEXT Search
**Description**: Sử dụng các query SQL (`LIKE`, `FULLTEXT INDEX`) để thực hiện tìm kiếm trực tiếp trên database

**Pros**:
- Không cần thêm bất kỳ service nào
- Dễ triển khai, tận dụng hạ tầng sẵn có
- Phù hợp với dataset nhỏ và query đơn giản
- Giảm độ phức tạp hệ thống tổng thể

**Cons**:
- Không hỗ trợ tốt các tính năng search nâng cao (fuzzy search, ranking)
- `LIKE` query có thể kém hiệu năng nếu không có index phù hợp
- Không tối ưu cho dữ liệu lớn hoặc search phức tạp

---

## Decision
We decided on **MySQL-based search (LIKE / FULLTEXT)** because it is the most **pragmatic and cost-effective solution** for the current system.

- Nhu cầu tìm kiếm của hệ thống chỉ ở mức cơ bản (keyword + filter), không yêu cầu các tính năng nâng cao như typo tolerance hoặc relevance ranking.
- MySQL có thể đáp ứng tốt yêu cầu này thông qua index và query tối ưu.
- Việc sử dụng Elasticsearch sẽ làm tăng đáng kể độ phức tạp (data synchronization, infrastructure, maintenance) mà không mang lại giá trị tương xứng trong giai đoạn hiện tại.

Giải pháp này giúp team tập trung vào việc hoàn thiện nghiệp vụ chính thay vì xử lý các vấn đề hạ tầng không cần thiết.

## Consequences

### Positive
- Giảm đáng kể độ phức tạp hệ thống
- Không cần triển khai thêm service ngoài MySQL
- Tăng tốc độ phát triển và giảm rủi ro kỹ thuật
- Dễ bảo trì và debug

### Negative
- Khả năng tìm kiếm bị giới hạn (không hỗ trợ fuzzy search, ranking nâng cao)
- Hiệu năng có thể giảm nếu dữ liệu tăng lớn và không tối ưu index

### Neutral
- Có thể nâng cấp sang Elasticsearch trong tương lai nếu:
  - Dataset tăng lớn
  - Yêu cầu search nâng cao xuất hiện
- Cần thiết kế index hợp lý (FULLTEXT, composite index) để đảm bảo hiệu năng