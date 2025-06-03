package com.zhengchalei.gox.modules.file.entity

import com.zhengchalei.gox.framework.jimmer.entity.BaseEntity
import org.babyfish.jimmer.sql.*
import java.time.LocalDateTime

/**
 * 文件信息实体
 */
@Entity
@Table(name = "extra_file_info")
interface FileInfo : BaseEntity {

    /**
     * 原始文件名
     */
    val originalName: String

    /**
     * 文件存储名称（UUID）
     */
    @Key
    val fileKey: String

    /**
     * 文件存储路径
     */
    val path: String

    /**
     * download url
     */
    val downloadUrl: String

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