create table if not exists authors
(
    id   varchar(36)  not null
    primary key,
    name varchar(255) not null,
    note varchar(255) null
    );

create table if not exists categories
(
    id   varchar(36)  not null
    primary key,
    name varchar(255) not null
    );

create table if not exists books
(
    id               varchar(36)      not null
    primary key,
    active           bit default b'1' not null,
    available_copies int              null,
    created_at       datetime(6)      null,
    description      text             null,
    isbn             varchar(20)      not null,
    number_of_pages  int              null,
    publisher        varchar(255)     null,
    thumbnail_url    text             null,
    title            varchar(255)     not null,
    total_copies     int              null,
    updated_at       datetime(6)      null,
    category_id      varchar(36)      null,
    constraint UKkibbepcitr0a3cpk3rfr7nihn
    unique (isbn),
    constraint FKleqa3hhc0uhfvurq6mil47xk0
    foreign key (category_id) references categories (id)
    );

create table if not exists book_authors
(
    book_id   varchar(36) not null,
    author_id varchar(36) not null,
    constraint FKbhqtkv2cndf10uhtknaqbyo0a
    foreign key (book_id) references books (id),
    constraint FKo86065vktj3hy1m7syr9cn7va
    foreign key (author_id) references authors (id)
    );

create table if not exists book_copies
(
    id      varchar(255)                                      not null
    primary key,
    barcode varchar(255)                                      not null,
    status  enum ('AVAILABLE', 'BORROWED', 'DAMAGED', 'LOST') null,
    book_id varchar(36)                                       null,
    constraint UKs7b8kwf25ikn6y6hbbso8fn2f
    unique (barcode),
    constraint FKhlawea8y2e2dv0ta58vc6f5nr
    foreign key (book_id) references books (id)
    );

create table if not exists users
(
    id           varchar(36)                                        not null
    primary key,
    borrow_quota int default 5                                      not null,
    created_at   datetime(6)                                        null,
    email        varchar(255)                                       not null,
    full_name    varchar(50)                                        not null,
    password     varchar(255)                                       not null,
    role         enum ('LIBRARIAN', 'SYSADMIN', 'USER')             null,
    status       enum ('ACTIVE', 'DELETED', 'PRIVATE', 'SUSPENDED') null,
    updated_at   datetime(6)                                        null,
    constraint UK6dotkott2kjsp8vw4d0m25fb7
    unique (email)
    );

create table if not exists borrow_records
(
    id            varchar(36)                               not null
    primary key,
    borrow_date   date                                      null,
    created_at    datetime(6)                               null,
    due_date      date                                      null,
    reminder_sent bit                                       not null,
    return_date   date                                      null,
    status        enum ('BORROWING', 'OVERDUE', 'RETURNED') null,
    updated_at    datetime(6)                               null,
    book_copy_id  varchar(255)                              not null,
    borrower_id   varchar(36)                               not null,
    librarian_id  varchar(36)                               not null,
    constraint FKhqf9ceuw2x6rxawa6s1e2fs1w
    foreign key (book_copy_id) references book_copies (id),
    constraint FKiejlerch9mckg3dkl9vrlqn32
    foreign key (borrower_id) references users (id),
    constraint FKjfib4mp8y7hhjihwx8uhdapxc
    foreign key (librarian_id) references users (id)
    );

create table if not exists borrow_audit_logs
(
    id           varchar(36)                    not null
    primary key,
    action       enum ('FORCE_RETURN', 'RENEW') null,
    created_at   datetime(6)                    null,
    note         varchar(255)                   null,
    borrow_id    varchar(36)                    not null,
    performed_by varchar(36)                    not null,
    constraint FKjvamc60mh15ts71h35s9ipcg4
    foreign key (borrow_id) references borrow_records (id),
    constraint FKph17wq9sqyb372us9w5oo82yu
    foreign key (performed_by) references users (id)
    );

create table if not exists notifications
(
    id         varchar(36)           not null
    primary key,
    created_at datetime(6)           null,
    sent       bit                   not null,
    sent_at    datetime(6)           null,
    type       enum ('DUE_REMINDER') null,
    user_id    varchar(36)           not null,
    constraint FK9y21adhxn0ayjhfocscqox7bh
    foreign key (user_id) references users (id)
    );

create table if not exists notification_logs
(
    id              varchar(36) not null
    primary key,
    attempt_no      int         not null,
    sent_at         datetime(6) null,
    success         bit         not null,
    notification_id varchar(36) not null,
    constraint FKnc1gydajjr1axlduw0ttn7nc9
    foreign key (notification_id) references notifications (id)
    );

