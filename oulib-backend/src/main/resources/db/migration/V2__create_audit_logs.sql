create table if not exists audit_logs
(
    id            bigint auto_increment
    primary key,
    user_id       bigint                                                      not null,
    action        enum ('CREATE', 'UPDATE', 'DELETE', 'LOGIN', 'LOGOUT')     not null,
    resource_type enum ('USER', 'BOOK', 'CATEGORY', 'BORROW_RECORD') not null,
    resource_id   bigint                                                      null,
    old_value     json                                                        null,
    new_value     json                                                        null,
    created_at    datetime(6)                                                 not null
    );

create index idx_audit_logs_user_id on audit_logs (user_id);
create index idx_audit_logs_created_at on audit_logs (created_at);
