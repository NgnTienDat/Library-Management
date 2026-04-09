# File Tree: Library-Management

```
├── .github                           # Cấu hình tự động hóa với GitHub Actions
│   └── workflows                     # Pipeline CI/CD
│       └── ci.yml   
├── docs                              # Tài liệu thiết kế, API, ADR,...
├── oulib-backend                     # Mã nguồn backend (Spring Boot)
│   ├── ...
│   ├── src
│   │   ├── main
│   │   │   ├── java
│   │   │   │   └── com
│   │   │   │       └── ou
│   │   │   │           └── oulib
│   │   │   │               ├── config          # Cấu hình ứng dụng (Security, JWT, Redis, RabbitMQ...)
│   │   │   │               ├── controller      # Định nghĩa các endpoint REST API
│   │   │   │               ├── dto             # Đối tượng Request/Response trao đổi dữ liệu
│   │   │   │               ├── entity          # Mô hình bảng dữ liệu (JPA Entity)
│   │   │   │               ├── enums           # Các hằng số trạng thái, vai trò, loại
│   │   │   │               ├── exception       # Xử lý lỗi và exception toàn cục
│   │   │   │               ├── infras          # Tích hợp hạ tầng bên ngoài (message broker, worker)
│   │   │   │               │   ├── consumer    # Nhận message từ queue
│   │   │   │               │   ├── event       # Đối tượng sự kiện/nội dung message
│   │   │   │               │   ├── producer    # Gửi message ra queue
│   │   │   │               │   └── worker      # Xử lý tác vụ nền/độc lập
│   │   │   │               ├── mapper          # Chuyển đổi entity <-> DTO
│   │   │   │               ├── repository      # Truy cập dữ liệu (JPA Repository)
│   │   │   │               ├── scheduler       # Tác vụ định kỳ (cron)
│   │   │   │               ├── security        # Xác thực, phân quyền, filter JWT
│   │   │   │               ├── service         # Xử lý nghiệp vụ chính
│   │   │   │               ├── specification   # Điều kiện truy vấn động
│   │   │   │               ├── utils           # Hàm tiện ích dùng chung
│   │   │   │               └── OulibBackendApplication.java
│   │   │   └── resources                       # File cấu hình và tài nguyên runtime
│   │   │       ├── db 
│   │   │       │   └── migration               # Script migration schema
│   │   │       │       └── v1_init.sql
│   │   │       └── application.yaml
│   │   └── test                                # Mã kiểm thử backend
│   ├── .dockerignore
│   ├── .gitattributes
│   ├── .gitignore
│   ├── Dockerfile
│   ├── HELP.md
│   ├── SETUP.md
│   ├── docker-compose.dev.yml
│   └── pom.xml
├── oulib-frontend                              # Mã nguồn frontend (Vite + React)
│   ├── ...
│   ├── src 
│   │   ├── api                                 # Gọi API backend
│   │   ├── components                          # Thành phần UI tái sử dụng
│   │   ├── contexts                            # State toàn cục bằng React Context
│   │   ├── hooks                               # Custom hooks
│   │   ├── pages                               
│   │   │   ├── admin                           # Màn hình dành cho Admin
│   │   │   ├── librarian                       # Màn hình dành cho Librarian
│   │   │   ├── user                            # Màn hình dành cho User
│   │   │   ├── LandingPage.jsx
│   │   │   ├── LoginPage.jsx
│   │   │   └── RegisterPage.jsx
│   │   ├── routes                              # Cấu hình điều hướng
│   │   ├── utils                               # Hàm tiện ích frontend
│   │   ├── App.jsx
│   │   ├── index.css
│   │   └── main.jsx
│   ├── .dockerignore
│   ├── .gitignore
│   ├── Dockerfile
│   ├── README.md
│   ├── eslint.config.js
│   ├── index.html
│   ├── package-lock.json
│   ├── package.json
│   └── vite.config.js
├── weekly-reports                              # Báo cáo tiến độ theo tuần
├── .gitignore
├── README.md
└── docker-compose.yml                         
```
