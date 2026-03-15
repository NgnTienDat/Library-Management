Giải thích các quyết định kiến trúc (Architecture Decision Records – ADRs)
Trong quá trình thiết kế hệ thống Library Management System, nhóm đã đưa ra một số quyết định kiến trúc quan trọng nhằm đảm bảo hệ thống có khả năng mở rộng, dễ bảo trì và dễ triển khai. Các quyết định này được ghi lại dưới dạng Architecture Decision Records (ADR) nhằm giải thích lý do lựa chọn công nghệ và kiến trúc cho hệ thống.

ADR 1: Sử dụng Spring Boot cho Backend
Status: Accepted
Context:
Hệ thống quản lý thư viện số cần một backend mạnh mẽ để xử lý các chức năng chính như quản lý sách, quản lý người dùng, mượn và trả sách, thống kê dữ liệu và đề xuất sách. Backend cần hỗ trợ xây dựng API nhanh chóng, dễ bảo trì và có khả năng mở rộng khi hệ thống phát triển.
Decision:
Nhóm quyết định sử dụng Spring Boot để xây dựng backend cho hệ thống. Spring Boot là một framework phổ biến trong hệ sinh thái Java, giúp đơn giản hóa quá trình phát triển ứng dụng web và RESTful API.
Consequences:
Ưu điểm:
•	Tăng tốc độ phát triển nhờ cấu hình tự động (auto configuration).
•	Dễ dàng xây dựng RESTful API.
•	Hệ sinh thái Spring hỗ trợ mạnh mẽ cho bảo mật, database và message queue.
•	Dễ tích hợp với các công nghệ khác như MySQL và RabbitMQ.
Nhược điểm:
•	Cấu hình ban đầu có thể phức tạp đối với người mới.
•	Tốn tài nguyên hơn so với một số framework backend nhẹ.

ADR 2: Sử dụng ReactJS cho Frontend
Status: Accepted
Context:
Hệ thống cần cung cấp giao diện web cho các đối tượng sử dụng như độc giả, thủ thư và quản trị viên. Giao diện cần đảm bảo khả năng tương tác nhanh, dễ sử dụng và hỗ trợ cập nhật dữ liệu theo thời gian thực thông qua API.
Decision:
Nhóm quyết định sử dụng ReactJS để phát triển frontend của hệ thống. ReactJS là thư viện JavaScript phổ biến cho phép xây dựng giao diện người dùng theo mô hình component.
Consequences:
Ưu điểm:
•	Tăng hiệu năng giao diện nhờ Virtual DOM.
•	Dễ tái sử dụng component trong nhiều phần của hệ thống.
•	Phù hợp để xây dựng các ứng dụng web hiện đại (Single Page Application).
•	Dễ dàng kết nối với backend thông qua REST API.
Nhược điểm:
•	Quản lý state có thể phức tạp khi ứng dụng lớn.
•	Người mới cần thời gian làm quen với ReactJS.

ADR 3: Sử dụng MySQL làm hệ quản trị cơ sở dữ liệu
Status: Accepted
Context:
Hệ thống cần lưu trữ nhiều loại dữ liệu có cấu trúc như:
•	Thông tin sách
•	Thông tin độc giả
•	Phiếu mượn và trả sách
•	Lịch sử giao dịch
•	Thống kê hệ thống
Do dữ liệu có quan hệ chặt chẽ với nhau nên cần sử dụng một hệ quản trị cơ sở dữ liệu quan hệ.
Decision:
Nhóm quyết định sử dụng MySQL làm hệ quản trị cơ sở dữ liệu chính cho hệ thống.
Consequences:
Ưu điểm:
•	Phổ biến và ổn định.
•	Phù hợp với dữ liệu quan hệ.
•	Hỗ trợ tốt các truy vấn SQL phức tạp.
•	Dễ triển khai và quản lý.
Nhược điểm:
•	Khả năng mở rộng ngang (horizontal scaling) hạn chế hơn so với một số hệ NoSQL.

ADR 4: Sử dụng RabbitMQ cho Message Queue
Status: Accepted
Context:
Một số chức năng của hệ thống cần xử lý bất đồng bộ (asynchronous) nhằm giảm tải cho backend chính, ví dụ:
•	Gửi email nhắc nhở sách sắp đến hạn trả.
•	Gửi thông báo cho người dùng.
•	Xử lý các tác vụ nền liên quan đến đề xuất sách.
Decision:
Nhóm quyết định sử dụng RabbitMQ làm hệ thống message queue để xử lý các tác vụ bất đồng bộ trong hệ thống.
Consequences:
Ưu điểm:
•	Giảm tải cho server backend chính.
•	Tăng hiệu năng hệ thống khi xử lý các tác vụ nền.
•	Tăng khả năng mở rộng và độ tin cậy của hệ thống.
Nhược điểm:
•	Cần triển khai và quản lý thêm một dịch vụ trung gian.
•	Tăng độ phức tạp trong kiến trúc hệ thống.

ADR 5: Sử dụng Docker và Docker Compose để triển khai hệ thống
Status: Accepted
Context:
Hệ thống gồm nhiều thành phần khác nhau như:
•	Backend (Spring Boot)
•	Frontend (ReactJS)
•	Database (MySQL)
•	Message Queue (RabbitMQ)
Nếu cài đặt thủ công từng thành phần trên mỗi máy có thể gây ra lỗi môi trường và khó khăn trong việc triển khai.
Decision:
Nhóm quyết định sử dụng Docker để đóng gói các thành phần của hệ thống dưới dạng container. Đồng thời sử dụng Docker Compose để quản lý và chạy nhiều container cùng lúc.
Consequences:
Ưu điểm:
•	Đảm bảo môi trường chạy đồng nhất trên mọi máy.
•	Dễ dàng triển khai và demo hệ thống.
•	Hỗ trợ tốt cho việc tích hợp CI/CD trong tương lai.
Nhược điểm:
•	Cần học thêm về Docker và cách cấu hình container.
•	Tăng độ phức tạp ban đầu của hệ thống.

