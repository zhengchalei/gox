CREATE TABLE IF NOT EXISTS `sys_user`
(
    `id`           BIGINT PRIMARY KEY auto_increment COMMENT '用户ID',
    `username`     VARCHAR(64) UNIQUE                                                                                NOT NULL COMMENT '用户名',
    `nickname`     VARCHAR(64)                                                                                       NOT NULL COMMENT '昵称',
    `avatar`       VARCHAR(255) DEFAULT 'https://i1.hdslb.com/bfs/face/57238d151820c8ba76ff31f88845b2a5200f1e72.jpg' NOT NULL COMMENT '头像',
    `email`        VARCHAR(64) COMMENT '邮箱',
    `phone`        VARCHAR(64) COMMENT '手机号',
    `enabled`      BOOLEAN      DEFAULT TRUE                                                                         NOT NULL COMMENT '是否启用',
    `password`     VARCHAR(255)                                                                                      NOT NULL COMMENT '密码(建议存储加密后的值)',
    `created_time` TIMESTAMP    DEFAULT CURRENT_TIMESTAMP                                                            NOT NULL COMMENT '创建时间',
    `updated_time` TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP                                NOT NULL COMMENT '更新时间',
    INDEX `idx_username` (`username`) COMMENT '用户名索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='系统用户表';

CREATE TABLE IF NOT EXISTS `sys_role`
(
    `id`           BIGINT PRIMARY KEY auto_increment COMMENT '角色ID',
    `name`         VARCHAR(64) UNIQUE                                              NOT NULL COMMENT '角色名称',
    `code`         VARCHAR(64) UNIQUE                                              NOT NULL COMMENT '角色编码',
    `description`  VARCHAR(255) COMMENT '角色描述',
    `enabled`      BOOLEAN   DEFAULT TRUE                                          NOT NULL COMMENT '是否启用',
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP                             NOT NULL COMMENT '创建时间',
    `updated_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL COMMENT '更新时间',
    INDEX `idx_role_code` (`code`) COMMENT '角色编码索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='系统角色表';

CREATE TABLE IF NOT EXISTS `sys_permission`
(
    `id`           BIGINT PRIMARY KEY auto_increment COMMENT '权限ID',
    `name`         VARCHAR(64) UNIQUE                                              NOT NULL COMMENT '权限名称',
    `code`         VARCHAR(64) UNIQUE                                              NOT NULL COMMENT '权限编码',
    `description`  VARCHAR(255) COMMENT '权限描述',
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP                             NOT NULL COMMENT '创建时间',
    `updated_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL COMMENT '更新时间',
    INDEX `idx_permission_code` (`code`) COMMENT '权限编码索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='系统权限表';

CREATE TABLE IF NOT EXISTS `sys_user_role`
(
    `id`           BIGINT PRIMARY KEY auto_increment COMMENT '关联ID',
    `user_id`      BIGINT                              NOT NULL COMMENT '用户ID',
    `role_id`      BIGINT                              NOT NULL COMMENT '角色ID',
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`) COMMENT '用户角色唯一约束',
    FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户角色关联表';

CREATE TABLE IF NOT EXISTS `sys_role_permission`
(
    `id`            BIGINT PRIMARY KEY auto_increment COMMENT '关联ID',
    `role_id`       BIGINT                              NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT                              NOT NULL COMMENT '权限ID',
    `created_time`  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`) COMMENT '角色权限唯一约束',
    FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`permission_id`) REFERENCES `sys_permission` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='角色权限关联表';
