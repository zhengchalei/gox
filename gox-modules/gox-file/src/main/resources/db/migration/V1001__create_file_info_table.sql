CREATE TABLE if not exists `extra_file_info`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `original_name` VARCHAR(255) NOT NULL COMMENT '原始文件名',
    `storage_name`  VARCHAR(64)  NOT NULL COMMENT '文件存储名称（UUID）',
    `path`          VARCHAR(255) NOT NULL COMMENT '文件存储路径',
    `extension`     VARCHAR(20)  NOT NULL COMMENT '文件后缀',
    `size`          BIGINT       NOT NULL COMMENT '文件大小（字节）',
    `mime_type`     VARCHAR(128) NOT NULL COMMENT 'MIME类型',
    `storage_type`  VARCHAR(20)  NOT NULL COMMENT '存储类型: LOCAL-本地存储, ALIYUN-阿里云OSS, TENCENT-腾讯云COS, MINIO-MinIO',
    `create_time`   DATETIME     NOT NULL COMMENT '创建时间',
    `update_time`   DATETIME     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_storage_name` (`storage_name`),
    INDEX `idx_original_name` (`original_name`),
    INDEX `idx_storage_type` (`storage_type`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='文件信息表';