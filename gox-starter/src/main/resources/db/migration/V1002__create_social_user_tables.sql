-- 社会化用户表
CREATE TABLE IF NOT EXISTS sys_social_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    uuid VARCHAR(255) NOT NULL COMMENT '第三方系统的唯一ID',
    source VARCHAR(50) NOT NULL COMMENT '第三方用户来源',
    access_token VARCHAR(255) NOT NULL COMMENT '用户的授权令牌',
    expire_in INT COMMENT '第三方用户的授权令牌的有效期',
    refresh_token VARCHAR(255) COMMENT '刷新令牌',
    open_id VARCHAR(255) COMMENT '第三方用户的open id',
    uid VARCHAR(255) COMMENT '第三方用户的ID',
    access_code VARCHAR(255) COMMENT '个别平台的授权信息',
    union_id VARCHAR(255) COMMENT '第三方用户的union id',
    scope VARCHAR(255) COMMENT '第三方用户授予的权限',
    token_type VARCHAR(255) COMMENT '个别平台的授权信息',
    id_token VARCHAR(255) COMMENT 'id token',
    mac_algorithm VARCHAR(255) COMMENT '小米平台用户的附带属性',
    mac_key VARCHAR(255) COMMENT '小米平台用户的附带属性',
    code VARCHAR(255) COMMENT '用户的授权code',
    oauth_token VARCHAR(255) COMMENT 'Twitter平台用户的附带属性',
    oauth_token_secret VARCHAR(255) COMMENT 'Twitter平台用户的附带属性',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_uuid_source (uuid, source) COMMENT 'UUID和来源的唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社会化用户表';

-- 社会化用户与系统用户关系表
CREATE TABLE IF NOT EXISTS sys_social_user_auth (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '系统用户ID',
    social_user_id BIGINT NOT NULL COMMENT '社会化用户ID',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_social (user_id, social_user_id) COMMENT '用户ID和社会化用户ID的唯一索引',
    KEY idx_social_user_id (social_user_id) COMMENT '社会化用户ID索引',
    CONSTRAINT fk_social_user_auth_user FOREIGN KEY (user_id) REFERENCES sys_user (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_social_user_auth_social FOREIGN KEY (social_user_id) REFERENCES sys_social_user (id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社会化用户与系统用户关系表'; 