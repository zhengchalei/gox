CREATE TABLE IF NOT EXISTS `sys_user`
(
    `id` BIGINT PRIMARY KEY auto_increment COMMENT '用户ID',
    `username`     VARCHAR(64) UNIQUE                                              NOT NULL COMMENT '用户名',
    `enabled`      BOOLEAN   DEFAULT TRUE                                          NOT NULL COMMENT '是否启用',
    `password`     VARCHAR(255)                                                    NOT NULL COMMENT '密码(建议存储加密后的值)',
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP                             NOT NULL COMMENT '创建时间',
    `updated_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL COMMENT '更新时间',
    INDEX `idx_username` (`username`) COMMENT '用户名索引'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='系统用户表';