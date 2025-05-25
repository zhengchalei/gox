CREATE TABLE IF NOT EXISTS sys_user
(
    id           BIGSERIAL PRIMARY KEY,
    username     TEXT UNIQUE                                        NOT NULL,
    nickname     TEXT                                               NOT NULL,
    avatar       TEXT                     DEFAULT 'https://i1.hdslb.com/bfs/face/57238d151820c8ba76ff31f88845b2a5200f1e72.jpg',
    email        TEXT,
    phone        TEXT,
    enabled      BOOLEAN                  DEFAULT TRUE              NOT NULL,
    password     TEXT                                               NOT NULL,
    created_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
) TABLESPACE pg_default;

COMMENT ON COLUMN sys_user.id IS '用户ID';
COMMENT ON COLUMN sys_user.username IS '用户名';
COMMENT ON COLUMN sys_user.nickname IS '昵称';
COMMENT ON COLUMN sys_user.avatar IS '头像';
COMMENT ON COLUMN sys_user.email IS '邮箱';
COMMENT ON COLUMN sys_user.phone IS '手机号';
COMMENT ON COLUMN sys_user.enabled IS '是否启用';
COMMENT ON COLUMN sys_user.password IS '密码(建议存储加密后的值)';
COMMENT ON COLUMN sys_user.created_time IS '创建时间';
COMMENT ON COLUMN sys_user.updated_time IS '更新时间';
CREATE INDEX idx_username ON sys_user (username);
COMMENT ON TABLE sys_user IS '系统用户表';

CREATE TABLE IF NOT EXISTS sys_role
(
    id           BIGSERIAL PRIMARY KEY,
    name         TEXT UNIQUE                                        NOT NULL,
    code         TEXT UNIQUE                                        NOT NULL,
    description  TEXT,
    enabled      BOOLEAN                  DEFAULT TRUE              NOT NULL,
    created_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
) TABLESPACE pg_default;

COMMENT ON COLUMN sys_role.id IS '角色ID';
COMMENT ON COLUMN sys_role.name IS '角色名称';
COMMENT ON COLUMN sys_role.code IS '角色编码';
COMMENT ON COLUMN sys_role.description IS '角色描述';
COMMENT ON COLUMN sys_role.enabled IS '是否启用';
COMMENT ON COLUMN sys_role.created_time IS '创建时间';
COMMENT ON COLUMN sys_role.updated_time IS '更新时间';
CREATE INDEX idx_role_code ON sys_role (code);
COMMENT ON TABLE sys_role IS '系统角色表';

CREATE TABLE IF NOT EXISTS sys_permission
(
    id           BIGSERIAL PRIMARY KEY,
    name         TEXT UNIQUE                                        NOT NULL,
    code         TEXT UNIQUE                                        NOT NULL,
    description  TEXT,
    created_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
) TABLESPACE pg_default;

COMMENT ON COLUMN sys_permission.id IS '权限ID';
COMMENT ON COLUMN sys_permission.name IS '权限名称';
COMMENT ON COLUMN sys_permission.code IS '权限编码';
COMMENT ON COLUMN sys_permission.description IS '权限描述';
COMMENT ON COLUMN sys_permission.created_time IS '创建时间';
COMMENT ON COLUMN sys_permission.updated_time IS '更新时间';
CREATE INDEX idx_permission_code ON sys_permission (code);
COMMENT ON TABLE sys_permission IS '系统权限表';

CREATE TABLE IF NOT EXISTS sys_user_role
(
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT NOT NULL,
    role_id      BIGINT NOT NULL,
    created_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_role UNIQUE (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES sys_user (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES sys_role (id) ON DELETE CASCADE
) TABLESPACE pg_default;

COMMENT ON COLUMN sys_user_role.id IS '关联ID';
COMMENT ON COLUMN sys_user_role.user_id IS '用户ID';
COMMENT ON COLUMN sys_user_role.role_id IS '角色ID';
COMMENT ON COLUMN sys_user_role.created_time IS '创建时间';
COMMENT ON TABLE sys_user_role IS '用户角色关联表';

CREATE TABLE IF NOT EXISTS sys_role_permission
(
    id            BIGSERIAL PRIMARY KEY,
    role_id       BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    created_time  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_role_permission UNIQUE (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES sys_role (id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES sys_permission (id) ON DELETE CASCADE
) TABLESPACE pg_default;

COMMENT ON COLUMN sys_role_permission.id IS '关联ID';
COMMENT ON COLUMN sys_role_permission.role_id IS '角色ID';
COMMENT ON COLUMN sys_role_permission.permission_id IS '权限ID';
COMMENT ON COLUMN sys_role_permission.created_time IS '创建时间';
COMMENT ON TABLE sys_role_permission IS '角色权限关联表';

-- 文件信息表
CREATE TABLE IF NOT EXISTS extra_file_info
(
    id            BIGSERIAL PRIMARY KEY,
    original_name TEXT                                               NOT NULL,
    file_key      TEXT                                               NOT NULL,
    path          TEXT                                               NOT NULL,
    download_url  TEXT                                               NOT NULL,
    extension     TEXT                                               NOT NULL,
    size          BIGINT                                             NOT NULL,
    mime_type     TEXT                                               NOT NULL,
    storage_type  TEXT                                               NOT NULL,
    created_time  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_time  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
) TABLESPACE pg_default;

COMMENT ON COLUMN extra_file_info.id IS '主键ID';
COMMENT ON COLUMN extra_file_info.original_name IS '原始文件名';
COMMENT ON COLUMN extra_file_info.file_key IS '文件存储名称（UUID）';
COMMENT ON COLUMN extra_file_info.path IS '文件存储路径';
COMMENT ON COLUMN extra_file_info.download_url IS '文件下载地址';
COMMENT ON COLUMN extra_file_info.extension IS '文件后缀';
COMMENT ON COLUMN extra_file_info.size IS '文件大小（字节）';
COMMENT ON COLUMN extra_file_info.mime_type IS 'MIME类型';
COMMENT ON COLUMN extra_file_info.storage_type IS '存储类型: LOCAL-本地存储, ALIYUN-阿里云OSS, TENCENT-腾讯云COS, MINIO-MinIO';
COMMENT ON COLUMN extra_file_info.created_time IS '创建时间';
COMMENT ON COLUMN extra_file_info.updated_time IS '更新时间';
CREATE UNIQUE INDEX uk_file_key ON extra_file_info (file_key);
CREATE INDEX idx_original_name ON extra_file_info (original_name);
CREATE INDEX idx_storage_type ON extra_file_info (storage_type);
COMMENT ON TABLE extra_file_info IS '文件信息表';

-- 社会化用户表
CREATE TABLE IF NOT EXISTS sys_social_user
(
    id                 BIGSERIAL PRIMARY KEY,
    uuid               TEXT                                               NOT NULL,
    source             TEXT                                               NOT NULL,
    access_token       TEXT                                               NOT NULL,
    expire_in          INTEGER,
    refresh_token      TEXT,
    open_id            TEXT,
    uid                TEXT,
    access_code        TEXT,
    union_id           TEXT,
    scope              TEXT,
    token_type         TEXT,
    id_token           TEXT,
    mac_algorithm      TEXT,
    mac_key            TEXT,
    code               TEXT,
    oauth_token        TEXT,
    oauth_token_secret TEXT,
    created_time       TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_time       TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
) TABLESPACE pg_default;

COMMENT ON COLUMN sys_social_user.id IS '主键ID';
COMMENT ON COLUMN sys_social_user.uuid IS '第三方系统的唯一ID';
COMMENT ON COLUMN sys_social_user.source IS '第三方用户来源';
COMMENT ON COLUMN sys_social_user.access_token IS '用户的授权令牌';
COMMENT ON COLUMN sys_social_user.expire_in IS '第三方用户的授权令牌的有效期';
COMMENT ON COLUMN sys_social_user.refresh_token IS '刷新令牌';
COMMENT ON COLUMN sys_social_user.open_id IS '第三方用户的open id';
COMMENT ON COLUMN sys_social_user.uid IS '第三方用户的ID';
COMMENT ON COLUMN sys_social_user.access_code IS '个别平台的授权信息';
COMMENT ON COLUMN sys_social_user.union_id IS '第三方用户的union id';
COMMENT ON COLUMN sys_social_user.scope IS '第三方用户授予的权限';
COMMENT ON COLUMN sys_social_user.token_type IS '个别平台的授权信息';
COMMENT ON COLUMN sys_social_user.id_token IS 'id token';
COMMENT ON COLUMN sys_social_user.mac_algorithm IS '小米平台用户的附带属性';
COMMENT ON COLUMN sys_social_user.mac_key IS '小米平台用户的附带属性';
COMMENT ON COLUMN sys_social_user.code IS '用户的授权code';
COMMENT ON COLUMN sys_social_user.oauth_token IS 'Twitter平台用户的附带属性';
COMMENT ON COLUMN sys_social_user.oauth_token_secret IS 'Twitter平台用户的附带属性';
COMMENT ON COLUMN sys_social_user.created_time IS '创建时间';
COMMENT ON COLUMN sys_social_user.updated_time IS '更新时间';
CREATE UNIQUE INDEX uk_uuid_source ON sys_social_user (uuid, source);
COMMENT ON TABLE sys_social_user IS '社会化用户表';

-- 社会化用户与系统用户关系表
CREATE TABLE IF NOT EXISTS sys_social_user_auth
(
    id             BIGSERIAL PRIMARY KEY,
    user_id        BIGINT                                             NOT NULL,
    social_user_id BIGINT                                             NOT NULL,
    created_time   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_time   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
        CONSTRAINT uk_user_social UNIQUE (user_id, social_user_id),
    CONSTRAINT fk_social_user_auth_user FOREIGN KEY (user_id) REFERENCES sys_user (id) ON DELETE CASCADE,
    CONSTRAINT fk_social_user_auth_social FOREIGN KEY (social_user_id) REFERENCES sys_social_user (id) ON DELETE CASCADE
) TABLESPACE pg_default;

COMMENT ON COLUMN sys_social_user_auth.id IS '主键ID';
COMMENT ON COLUMN sys_social_user_auth.user_id IS '系统用户ID';
COMMENT ON COLUMN sys_social_user_auth.social_user_id IS '社会化用户ID';
COMMENT ON COLUMN sys_social_user_auth.created_time IS '创建时间';
COMMENT ON COLUMN sys_social_user_auth.updated_time IS '更新时间';
CREATE INDEX idx_social_user_id ON sys_social_user_auth (social_user_id);
COMMENT ON TABLE sys_social_user_auth IS '社会化用户与系统用户关系表';
