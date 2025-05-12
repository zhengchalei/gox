create table if not exists `sys_user`
(
    id         bigint primary key comment 'id',
    username   varchar(255) not null comment '用户名',
    enabled    boolean      not null comment '是否启用',
    password   varchar(255) not null comment '密码',
    created_time timestamp not null comment '创建时间' default current_timestamp,
    updated_time timestamp not null comment '更新时间'
) comment '系统用户表';