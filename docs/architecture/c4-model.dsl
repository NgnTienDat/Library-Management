workspace {

    model {
        reader = person "Độc giả" "Tra cứu sách, xem chi tiết, xem đề xuất, nhận thông báo."
        librarian = person "Thủ thư" "Quản lý sách, ghi nhận mượn/trả sách, xem báo cáo"
        admin = person "Admin" "Quản trị hệ thống, cấu hình và giám sát."

        emailService = softwareSystem "Email Service" "SMTP provider (mock trong MVP)." 

        librarySystem = softwareSystem "Library Management System" "Hệ thống quản lý thư viện số" {

            webApp = container "Web Application" "Giao diện người dùng cho Độc giả / Thủ thư / Admin." "ReactJS"

            apiServer = container "API Server" "REST API, business logic, scheduler, OpenAPI/Swagger." "Spring Boot" {
                bookController = component "BookController" "REST Controller" "Endpoints: /api/books (CRUD, list, search, filter)."
                borrowController = component "BorrowController" "REST Controller" "Endpoints: /api/borrows (ghi nhận mượn/trả, lịch sử)."
                notificationController = component "NotificationController" "REST Controller" "Admin: xem và gửi lại thông báo."
                reportController = component "ReportController" "REST Controller" "Endpoints: /api/reports (top-books, inventory)."
                recommendationController = component "RecommendationController" "REST Controller" "Endpoints: /api/recommendations."

                bookService = component "BookService" "Service" "Quản lý sách, phân loại, tìm kiếm MySQL-based search."
                borrowService = component "BorrowService" "Service" "Ghi nhận mượn/trả, transaction, cập nhật số lượng sách."
                notificationService = component "NotificationService" "Service" "Tạo notification records, publish event sang RabbitMQ."
                reportService = component "ReportService" "Service" "Tổng hợp báo cáo từ borrow records và inventory."
                recommendationService = component "RecommendationService" "Service" "Đề xuất sách theo sách phổ biến / cùng thể loại / xu hướng gần đây."

                bookRepository = component "BookRepository" "JPA Repository" "Truy cập books, categories, authors, book_authors."
                borrowRepository = component "BorrowRepository" "JPA Repository" "Truy cập borrow_records và borrow_audit_logs."
                notificationRepository = component "NotificationRepository" "JPA Repository" "Truy cập notifications và notification_logs."
                userRepository = component "UserRepository" "JPA Repository" "Truy cập users."

                notificationScheduler = component "NotificationScheduler" "Scheduled Job" "Job định kỳ: kiểm tra sách sắp đến hạn và tạo reminder."
            }

            notificationWorker = container "Notification Worker" "Tiêu thụ message từ RabbitMQ, xử lý reminder và ghi log." "Spring Boot Worker"

            mysql = container "MySQL" "Lưu dữ liệu sách, người dùng, mượn/trả, thông báo, audit log." "MySQL"
            redis = container "Redis" "Cache cho top-books, recommendation, dữ liệu truy vấn thường xuyên." "Redis"
            rabbitmq = container "RabbitMQ" "Message broker cho notification events và background jobs." "RabbitMQ"
        }

        reader -> webApp "Sử dụng qua trình duyệt" "HTTPS"
        librarian -> webApp "Sử dụng để quản lý nghiệp vụ thư viện" "HTTPS"
        admin -> webApp "Sử dụng để quản trị hệ thống" "HTTPS"

        webApp -> apiServer "Gọi REST API" "HTTPS/JSON"

        apiServer -> mysql "Đọc/Ghi dữ liệu" "JDBC/ORM"
        apiServer -> redis "Cache dữ liệu truy vấn nhanh" "Redis protocol"
        apiServer -> rabbitmq "Publish notification events" "AMQP"

        notificationWorker -> rabbitmq "Consume events" "AMQP"
        notificationWorker -> mysql "Ghi notification logs, cập nhật trạng thái thông báo" "JDBC"
        notificationWorker -> emailService "Gửi email nhắc hạn (mock/log)" "SMTP"

        bookController -> bookService "Calls"
        borrowController -> borrowService "Calls"
        notificationController -> notificationService "Calls"
        reportController -> reportService "Calls"
        recommendationController -> recommendationService "Calls"

        bookService -> bookRepository "Uses"
        borrowService -> borrowRepository "Uses"
        notificationService -> notificationRepository "Uses"
        reportService -> borrowRepository "Read borrow data"
        reportService -> bookRepository "Read inventory data"
        recommendationService -> bookRepository "Uses"
        recommendationService -> borrowRepository "Uses"
        recommendationService -> redis "Cache recommendation"

        bookRepository -> mysql "Read/Write"
        borrowRepository -> mysql "Read/Write"
        notificationRepository -> mysql "Read/Write"
        userRepository -> mysql "Read/Write"

        notificationScheduler -> notificationService "Trigger"
        notificationScheduler -> borrowRepository "Check due_date"
    }

    views {
        systemContext librarySystem {
            include *
            autolayout lr
        }

        container librarySystem {
            include *
            autolayout lr
        }

        component apiServer {
            include *
            autolayout lr
        }
    }
}