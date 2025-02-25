-- auto-generated definition
CREATE DATABASE ucenter
    CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;

create table user
(
    id            bigint auto_increment
        primary key,
    nick_name     varchar(256)                        null comment '用户昵称',
    avatar_url    varchar(1024)                       null comment '头像',
    gender        tinyint                             null comment '性别 1-男 0-女',
    user_account  varchar(20)                         null comment '用户账号',
    user_password varchar(512)                        not null comment '用户密码',
    state         tinyint   default 1                 null comment '用户状态 1-正常',
    phone_number  varchar(128)                        null comment '手机号',
    email         varchar(512)                        null comment '邮箱',
    create_time   timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_deleted    tinyint   default 0                 not null comment '是否逻辑删除 0-否',
    user_role     int       default 0                 null comment '用户角色 0-普通用户 1-管理员',
    planet_code   varchar(512)                        null comment '星球编号'
)
    comment '用户表'
    ENGINE=InnoDB
    CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;
