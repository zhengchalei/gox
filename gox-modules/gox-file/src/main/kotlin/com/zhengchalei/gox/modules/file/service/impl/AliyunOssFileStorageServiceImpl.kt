package com.zhengchalei.gox.modules.file.service.impl

import com.aliyun.oss.OSS
import com.aliyun.oss.OSSClientBuilder
import com.aliyun.oss.model.ObjectMetadata
import com.zhengchalei.gox.modules.file.entity.FileInfo
import com.zhengchalei.gox.modules.file.entity.StorageType
import com.zhengchalei.gox.modules.file.entity.dto.FileInfoDetailDTO
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream
import java.time.Duration
import java.util.*

/**
 * 阿里云OSS文件存储服务实现
 */
@Service
@ConditionalOnProperty(prefix = "gox.file", name = ["storageType"], havingValue = "aliyun")
class AliyunOssFileStorageServiceImpl : AbstractFileStorageService() {

    private lateinit var ossClient: OSS

    @PostConstruct
    fun init() {
        ossClient = OSSClientBuilder().build(
            fileProperties.aliyun.endpoint,
            fileProperties.aliyun.accessKeyId,
            fileProperties.aliyun.accessKeySecret
        )

        // 确保存储桶存在
        if (!ossClient.doesBucketExist(fileProperties.aliyun.bucketName)) {
            ossClient.createBucket(fileProperties.aliyun.bucketName)
        }
    }

    @PreDestroy
    fun destroy() {
        ossClient.shutdown()
    }

    /**
     * 获取存储类型
     */
    override fun getStorageType(): StorageType = StorageType.ALIYUN

    /**
     * 上传文件
     */
    override fun upload(file: MultipartFile): FileInfo {
        val originalFilename = file.originalFilename ?: "unknown"
        val extension = getExtension(originalFilename)
        val path = generatePath()
        val fileKey = generateFileKey(extension)
        val objectName = path + fileKey

        // 设置元数据
        val metadata = ObjectMetadata()
        metadata.contentLength = file.size
        metadata.contentType = file.contentType ?: "application/octet-stream"

        // 上传文件
        ossClient.putObject(fileProperties.aliyun.bucketName, objectName, file.inputStream, metadata)

        return createFileInfo(
            originalFilename = originalFilename,
            fileKey = fileKey,
            path = path,
            size = file.size,
            mimeType = file.contentType ?: "application/octet-stream"
        )
    }

    /**
     * 上传文件流
     */
    override fun upload(inputStream: InputStream, originalFilename: String, size: Long, mimeType: String): FileInfo {
        val extension = getExtension(originalFilename)
        val path = generatePath()
        val fileKey = generateFileKey(extension)
        val objectName = path + fileKey

        // 设置元数据
        val metadata = ObjectMetadata()
        metadata.contentLength = size
        metadata.contentType = mimeType

        // 上传文件
        ossClient.putObject(fileProperties.aliyun.bucketName, objectName, inputStream, metadata)

        return createFileInfo(
            originalFilename = originalFilename,
            fileKey = fileKey,
            path = path,
            size = size,
            mimeType = mimeType,
        )
    }

    override fun getDownloadUrl(fileInfoDetailDTO: FileInfoDetailDTO): String {
        if (fileProperties.aliyun.domain != null) {
            return "${fileProperties.aliyun.domain}/${fileInfoDetailDTO.path}${fileInfoDetailDTO.fileKey}"
        }

        return "https://${fileProperties.aliyun.bucketName}.${fileProperties.aliyun.endpoint}/${fileInfoDetailDTO.path}${fileInfoDetailDTO.fileKey}"
    }

    override fun getTemporaryUrl(
        fileInfoDetailDTO: FileInfoDetailDTO,
        duration: Duration
    ): String {
        val objectName = fileInfoDetailDTO.path + fileInfoDetailDTO.fileKey
        val expiration = Date(System.currentTimeMillis() + duration.toMillis())

        return ossClient.generatePresignedUrl(
            fileProperties.aliyun.bucketName,
            objectName,
            expiration
        ).toString()
    }

    /**
     * 删除文件
     */
    override fun delete(fileInfoDetailDTO: FileInfoDetailDTO) {
        val objectName = fileInfoDetailDTO.path + fileInfoDetailDTO.fileKey
        ossClient.deleteObject(fileProperties.aliyun.bucketName, objectName)
        fileInfoRepository.deleteById(fileInfoDetailDTO.id)
    }
} 