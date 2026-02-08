# YÊU CẦU CHỨC NĂNG VÀ PHI CHỨC NĂNG — LIBRARY MANAGEMENT

**Thành viên:** Phạm Minh Hạnh (MSSV: 2151053015)  
**Nhóm 5:** Library Management — Đề tài C1: Hệ thống quản lý thư viện số

---

## 1. Nhiệm vụ

- Chuẩn hóa yêu cầu  
- Giữ phạm vi MVP không bị phình to  

### Công việc cụ thể
- Viết Yêu cầu chức năng  

---

## 2. Ghi chú thuật ngữ

- **ISBN (International Standard Book Number):** Mã số tiêu chuẩn quốc tế duy nhất để xác định một quyển sách.  
- **Preconditions:** Điều kiện tiên quyết.  
- **AC (Acceptance Criteria):** Tiêu chí chấp nhận.  

---

# Module: Book — Quản lý sách (CRUD)

---

## FR-01: Thêm sách

**Mô tả:**  
Thủ thư/Admin có thể tạo bản ghi sách với các trường bắt buộc:  
`ISBN, title, authors, publisher, numberOfPages, category, total_copies, description`.

**Actors:** Admin, Thủ thư.  

**Preconditions:**  
- User đã xác thực.  
- Sách chưa tồn tại trong hệ thống.  

**AC:**  
- Form tạo sách có các trường bắt buộc.  
- Sau khi tạo, sách xuất hiện trong danh sách với trạng thái *available*.  

---

## FR-02: Xem sách (List + Detail)

**Mô tả:**  
Người dùng (Độc giả/Thủ thư/Admin) có thể:  
- Xem danh sách sách (phân trang)  
- Tìm kiếm theo tiêu đề hoặc tác giả  
- Lọc theo thể loại hoặc nhà xuất bản  
- Xem chi tiết thông tin sách  

**Actors:** Tất cả user.  

**AC:**  
- Phân trang mặc định 10 sách/trang.  
- Tìm kiếm theo title hoặc author.  
- Lọc theo category hoặc publisher.  
- Xem đầy đủ thông tin chi tiết sách.  

---

## FR-03: Sửa sách

**Mô tả:**  
Admin/Thủ thư có thể chỉnh sửa thông tin sách.  

**Actors:** Admin, Thủ thư.  

**Preconditions:** Sách đã tồn tại.  

**AC:**  
- Thông tin mới được lưu vào DB.  
- Hiển thị cập nhật ngay lập tức.  
- Lưu thời gian và người cập nhật.  

---

## FR-04: Xóa sách

**Mô tả:**  
Admin/Thủ thư có thể xóa sách nếu không có bản sao đang được mượn.  

**Actors:** Admin, Thủ thư.  

**Preconditions:**  
- `available_copies = total_copies` hoặc `total_copies = 0`.  

**AC:**  
- Nếu sách đang mượn → hiển thị lỗi.  
- Nếu hợp lệ → xóa khỏi DB và danh sách.  

---

## FR-05: Xem trạng thái sách

**Mô tả:**  
Hiển thị trạng thái sách:

**Thủ thư/Admin thấy:**  
- `total_copies`  
- `available_copies`  
- `borrowed_copies`  

**Độc giả thấy:**  
- Còn hàng / Hết hàng  

**Actors:** Tất cả user.  

**AC:**  
- Trạng thái cập nhật sau mỗi lần mượn/trả.  
- Hiển thị ở list và detail.  

---

# Module: Borrow — Mượn/Trả sách

---

## FR-06: Mượn sách

**Mô tả:**  
Độc giả đã login có thể mượn nếu `available_copies > 0`.  
Tạo borrow record:  
`borrow_id, user_id, book_id, borrow_date, due_date, status="borrowed"`.

**Actors:** Độc giả.  

**Preconditions:**  
- User đã xác thực  
- `available_copies > 0`  

**AC:**  
- Không cho mượn nếu hết sách.  
- `available_copies - 1`.  
- Lưu borrow record.  

---

## FR-07: Trả sách

**Mô tả:**  
Cập nhật borrow record với `return_date`, `status="returned"`.  
Tăng `available_copies +1`.  

**Actors:** Độc giả, Thủ thư/Admin.  

**Preconditions:** Có borrow record với status="borrowed".  

**AC:**  
- Cập nhật trạng thái trả đúng hạn hoặc trễ.  
- Tăng available_copies.  
- Nếu trả sai trạng thái → báo lỗi.  

---

## FR-08: Lịch sử mượn

**Mô tả:**  
Độc giả xem lịch sử mượn cá nhân (borrowed/returned/late).  

**Actors:** Độc giả.  

**AC:**  
- Chỉ hiển thị bản ghi của user.  
- Phân trang 10 bản ghi/trang.  

---

## FR-09: Quản lý borrow records

**Mô tả:**  
Thủ thư/Admin có thể gia hạn hoặc hối trả.  

**Actors:** Thủ thư, Admin.  

**AC:**  
- Gia hạn theo chính sách (+7 ngày).  
- Hối trả cập nhật giống FR-07.  
- Ghi log audit.  

---

# Module: Notification

---

## FR-10: Nhắc hạn tự động

**Mô tả:**  
Tự động xác định borrow gần đến hạn (due_date - 2 days) và gửi thông báo mock.  

**Actors:** Hệ thống.  

**AC:**  
- Tạo bản ghi notification.  
- Mock email/log.  
- Có flag `sent=true/false`.  

---

## FR-11: Gửi lại thông báo

**Mô tả:**  
Admin/Thủ thư có thể resend notification.  

**Actors:** Thủ thư, Admin.  

**AC:**  
- Ghi log resend.  
- Cập nhật `sent_at`.  

---

# Module: Reporting

---

## FR-12: Báo cáo top sách mượn

**Mô tả:**  
Báo cáo top sách mượn nhiều nhất theo tuần/tháng.  

**Actors:** Thủ thư, Admin.  

**AC:**  
- Sắp xếp theo borrow_count giảm dần.  
- Có bộ lọc thời gian.  

---

## FR-13: Thống kê kho sách

**Mô tả:**  
Hiển thị tổng số sách và số sách đang mượn.  

**Actors:** Thủ thư, Admin.  

**AC:**  
- Trả số liệu chính xác từ DB.  
- Có thời điểm generate report.  

---

# Module: Recommendation

---

## FR-14: Đề xuất sách

**Mô tả:**  
Đề xuất sách dựa trên:

- Sách phổ biến  
- Sách cùng thể loại  
- Xu hướng gần đây  

**Actors:** Độc giả.  

**AC:**  
- Hiển thị tối đa 10 sách đề xuất.  

---

# 3. Yêu cầu Phi chức năng (Non-Functional Requirements)

---

## NFR-01 Hiệu năng (Performance)

- API list/search < 1 giây với ~1000 sách  
- Hỗ trợ pagination  

---

## NFR-02 Bảo mật (Security)

- Login email/password  
- Password hash  
- Chức năng quản lý chỉ cho Admin/Thủ thư  

---

## NFR-03 Triển khai (Deployability)

- Docker + Docker Compose  
- Hướng dẫn chạy bằng một lệnh  

---

## NFR-04 Bảo trì (Maintainability)

- Layered Architecture  
- Có README cấu trúc dự án  

---

## NFR-05 Độ tin cậy dữ liệu

- Transaction cho borrow/return  
- Không xảy ra số lượng âm  

---

## NFR-06 Khả năng sử dụng

- UI đơn giản  
- Thông báo success/error  

---

# 4. Chức năng BẮT BUỘC (In Scope)

- Quản lý sách (CRUD)  
- Tìm kiếm, phân loại sách  
- Mượn/trả sách  
- Nhắc nhở sách sắp hết hạn  
- Thống kê, báo cáo  
- Đề xuất sách (rule-based)  

---

# 5. Chức năng KHÔNG LÀM (Out of Scope)

- Không làm mã QR check-in độc giả  
- Không làm đề xuất sách tích hợp AI (AI recommendation)  
- Không tích hợp thanh toán trực tuyến (online payment)  
- Không phân quyền nâng cao (advanced RBAC)  
- Không tối ưu mở rộng quy mô lớn (large-scale scalability)  
- Không phát triển ứng dụng mobile  
- Không tích hợp phần cứng thư viện (RFID, barcode scanner)  
