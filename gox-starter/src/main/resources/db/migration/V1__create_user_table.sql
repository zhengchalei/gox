create table if not exists `user`
(
    id         bigint primary key comment 'id',
    username   varchar(255) not null comment '用户名',
    enabled    boolean      not null comment '是否启用',
    password   varchar(255) not null comment '密码',
    created_at timestamp    not null comment '创建时间',
    updated_at timestamp    not null comment '更新时间'
)  comment '用户表';