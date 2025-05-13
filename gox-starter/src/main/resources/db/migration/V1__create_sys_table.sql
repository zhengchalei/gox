CREATE TABLE IF NOT EXISTS `sys_user`
(
    `id`           BIGINT PRIMARY KEY auto_increment COMMENT '用户ID',
    `username`     VARCHAR(64) UNIQUE                                              NOT NULL COMMENT '用户名',
    `enabled`      BOOLEAN   DEFAULT TRUE                                          NOT NULL COMMENT '是否启用',
    `password`     VARCHAR(255)                                                    NOT NULL COMMENT '密码(建议存储加密后的值)',
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP                             NOT NULL COMMENT '创建时间',
    `updated_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL COMMENT '更新时间',
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

-- 初始化管理员用户
INSERT INTO `sys_user` (`username`, `enabled`, `password`)
VALUES ('admin', TRUE, 'encrypted_password_here');

-- 初始化角色数据
INSERT INTO `sys_role` (`name`, `code`, `description`, `enabled`)
VALUES ('管理员', 'ADMIN', '系统管理员角色，拥有最高权限', TRUE),
       ('用户', 'USER', '普通用户角色，拥有基本权限', TRUE);

-- 初始化权限数据
INSERT INTO `sys_permission` (`name`, `code`, `description`)
VALUES ('所有权限', 'ALL', '拥有系统所有权限'),
       ('查看用户', 'USER_VIEW', '查看用户信息权限'),
       ('编辑用户', 'USER_EDIT', '编辑用户信息权限'),
       ('查看角色', 'ROLE_VIEW', '查看角色信息权限'),
       ('编辑角色', 'ROLE_EDIT', '编辑角色信息权限');

-- 初始化用户角色关联数据 (假设有一个用户ID为1的管理员用户)
INSERT INTO `sys_user_role` (`user_id`, `role_id`)
VALUES (1, 1);

-- 初始化角色权限关联数据
-- 管理员角色拥有所有权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
VALUES (1, 1);

-- 用户角色拥有基本查看权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
VALUES (2, 2),
       (2, 4);