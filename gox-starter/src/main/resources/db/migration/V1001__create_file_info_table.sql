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
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP                             NOT NULL COMMENT '创建时间',
    `updated_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_storage_name` (`storage_name`),
    INDEX `idx_original_name` (`original_name`),
    INDEX `idx_storage_type` (`storage_type`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='文件信息表';

INSERT INTO extra_file_info (original_name, storage_name, path, extension, size, mime_type, storage_type, created_time, updated_time) VALUES ('下载.png', '44306ce9dde54c618670513c5b6df204.png', '2025/05/24/', 'png', 4347, 'image/png', 'LOCAL', '2025-05-23 18:49:38', '2025-05-23 18:49:38');
INSERT INTO extra_file_info (original_name, storage_name, path, extension, size, mime_type, storage_type, created_time, updated_time) VALUES ('微信图片_20250421205454.jpg', 'd93b0053c825438aa6b35d5fcff16ffc.jpg', '2025/05/24/', 'jpg', 249306, 'image/jpeg', 'LOCAL', '2025-05-23 18:49:38', '2025-05-23 18:49:38');
INSERT INTO extra_file_info (original_name, storage_name, path, extension, size, mime_type, storage_type, created_time, updated_time) VALUES ('微信图片_20250421205203.jpg', '26a52d9022f347bb9cb4adb9c4835b92.jpg', '2025/05/24/', 'jpg', 568353, 'image/jpeg', 'LOCAL', '2025-05-23 18:49:38', '2025-05-23 18:49:38');
INSERT INTO extra_file_info (original_name, storage_name, path, extension, size, mime_type, storage_type, created_time, updated_time) VALUES ('微信图片_20250420160910.jpg', '9e62968454f9487a8ae28862d31dbbcc.jpg', '2025/05/24/', 'jpg', 499938, 'image/jpeg', 'LOCAL', '2025-05-23 18:49:38', '2025-05-23 18:49:38');
INSERT INTO extra_file_info (original_name, storage_name, path, extension, size, mime_type, storage_type, created_time, updated_time) VALUES ('微信图片_20250421205212.jpg', 'c9f0355610b84602872e4c5283c58765.jpg', '2025/05/24/', 'jpg', 455943, 'image/jpeg', 'LOCAL', '2025-05-23 18:49:38', '2025-05-23 18:49:38');
