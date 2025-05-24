package com.zhengchalei.gox.modules.file.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * 文件存储配置
 */
@Component
@ConfigurationProperties(prefix = "gox.file")
class FileProperties {
    /**
     * 存储类型：local, aliyun, tencent, minio
     */
    var storageType: String = "local"

    /**
     * 域名
     */
    var domain: String = "http://localhost:8080"

    /**
     * 本地存储配置
     */
    val local = LocalStorageProperties()

    /**
     * 阿里云OSS配置
     */
    val aliyun = AliyunOssProperties()

    /**
     * 腾讯云COS配置
     */
    val tencent = TencentCosProperties()

    /**
     * MinIO配置
     */
    val minio = MinioProperties()
}

/**
 * 本地存储配置
 */
class LocalStorageProperties {
    /**
     * 基础存储路径
     */
    var basePath: String = "uploads"
}

/**
 * 阿里云OSS配置
 */
class AliyunOssProperties {
    /**
     * 访问密钥ID
     */
    var accessKeyId: String = ""

    /**
     * 访问密钥密码
     */
    var accessKeySecret: String = ""

    /**
     * 端点
     */
    var endpoint: String = ""

    /**
     * 存储桶名称
     */
    var bucketName: String = ""

    /**
     * 自定义域名（可选）
     */
    var domain: String? = null
}

/**
 * 腾讯云COS配置
 */
class TencentCosProperties {
    /**
     * 访问密钥ID
     */
    var secretId: String = ""

    /**
     * 访问密钥密码
     */
    var secretKey: String = ""

    /**
     * 区域
     */
    var region: String = ""

    /**
     * 存储桶名称
     */
    var bucketName: String = ""

    /**
     * 自定义域名（可选）
     */
    var domain: String? = null
}

/**
 * MinIO配置
 */
class MinioProperties {
    /**
     * 端点
     */
    var endpoint: String = ""

    /**
     * 访问密钥ID
     */
    var accessKey: String = ""

    /**
     * 访问密钥密码
     */
    var secretKey: String = ""

    /**
     * 存储桶名称
     */
    var bucketName: String = ""

    /**
     * 是否使用HTTPS
     */
    var secure: Boolean = false
} 