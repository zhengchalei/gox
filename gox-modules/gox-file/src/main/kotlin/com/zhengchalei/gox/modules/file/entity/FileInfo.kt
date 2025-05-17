package com.zhengchalei.gox.modules.file.entity

import org.babyfish.jimmer.sql.*
import java.time.LocalDateTime

/**
 * 文件信息实体
 */
@Entity
@Table(name = "extra_file_info")
interface FileInfo {
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long
    
    /**
     * 原始文件名
     */
    val originalName: String
    
    /**
     * 文件存储名称（UUID）
     */
    @Key
    val storageName: String
    
    /**
     * 文件存储路径
     */
    val path: String
    
    /**
     * 文件后缀
     */
    val extension: String
    
    /**
     * 文件大小（字节）
     */
    val size: Long
    
    /**
     * MIME类型
     */
    val mimeType: String
    
    /**
     * 存储类型
     */
    val storageType: StorageType
    
    /**
     * 创建时间
     */
    val createTime: LocalDateTime
    
    /**
     * 更新时间
     */
    val updateTime: LocalDateTime
}

/**
 * 存储类型枚举
 */
@EnumType(value = EnumType.Strategy.NAME)
enum class StorageType {
    /**
     * 本地存储
     */
    LOCAL,
    
    /**
     * 阿里云OSS
     */
    ALIYUN,
    
    /**
     * 腾讯云COS
     */
    TENCENT,
    
    /**
     * MinIO
     */
    MINIO
} 