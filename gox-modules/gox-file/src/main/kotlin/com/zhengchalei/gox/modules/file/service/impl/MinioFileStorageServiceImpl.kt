package com.zhengchalei.gox.modules.file.service.impl

import com.zhengchalei.gox.modules.file.entity.FileInfo
import com.zhengchalei.gox.modules.file.entity.StorageType
import com.zhengchalei.gox.modules.file.entity.dto.FileInfoDetailDTO
import io.minio.*
import io.minio.http.Method
import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream
import java.time.Duration
import java.util.concurrent.TimeUnit

/**
 * MinIO文件存储服务实现
 */
@Service
@ConditionalOnProperty(prefix = "gox.file", name = ["storageType"], havingValue = "minio")
class MinioFileStorageServiceImpl : AbstractFileStorageService() {

    private lateinit var minioClient: MinioClient

    @PostConstruct
    fun init() {
        // 初始化MinIO客户端
        minioClient = MinioClient.builder()
            .endpoint(fileProperties.minio.endpoint)
            .credentials(fileProperties.minio.accessKey, fileProperties.minio.secretKey)
            .build()

        // 确保存储桶存在
        val bucketExists = minioClient.bucketExists(
            BucketExistsArgs.builder()
                .bucket(fileProperties.minio.bucketName)
                .build()
        )

        if (!bucketExists) {
            minioClient.makeBucket(
                MakeBucketArgs.builder()
                    .bucket(fileProperties.minio.bucketName)
                    .build()
            )
        }
    }

    /**
     * 获取存储类型
     */
    override fun getStorageType(): StorageType = StorageType.MINIO

    /**
     * 上传文件
     */
    override fun upload(file: MultipartFile): FileInfo {
        val originalFilename = file.originalFilename ?: "unknown"
        val extension = getExtension(originalFilename)
        val path = generatePath()
        val fileKey = generateFileKey(extension)
        val objectName = path + fileKey

        // 上传文件
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(fileProperties.minio.bucketName)
                .`object`(objectName)
                .stream(file.inputStream, file.size, -1)
                .contentType(file.contentType ?: "application/octet-stream")
                .build()
        )

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

        // 上传文件
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(fileProperties.minio.bucketName)
                .`object`(objectName)
                .stream(inputStream, size, -1)
                .contentType(mimeType)
                .build()
        )

        // 保存文件信息
        return createFileInfo(
            originalFilename = originalFilename,
            fileKey = fileKey,
            path = path,
            size = size,
            mimeType = mimeType
        )
    }

    /**
     * 获取文件下载URL
     */
    override fun getDownloadUrl(fileInfoDetailDTO: FileInfoDetailDTO): String {
        val objectName = fileInfoDetailDTO.path + fileInfoDetailDTO.fileKey

        return minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(fileProperties.minio.bucketName)
                .`object`(objectName)
                .expiry(7, TimeUnit.DAYS) // 默认7天有效期
                .build()
        )
    }

    /**
     * 获取文件临时访问URL
     */
    override fun getTemporaryUrl(fileInfoDetailDTO: FileInfoDetailDTO, duration: Duration): String {
        val objectName = fileInfoDetailDTO.path + fileInfoDetailDTO.fileKey

        return minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(fileProperties.minio.bucketName)
                .`object`(objectName)
                .expiry(duration.seconds.toInt(), TimeUnit.SECONDS)
                .build()
        )
    }

    /**
     * 删除文件
     */
    override fun delete(fileInfoDetailDTO: FileInfoDetailDTO) {
        val objectName = fileInfoDetailDTO.path + fileInfoDetailDTO.fileKey

        minioClient.removeObject(
            RemoveObjectArgs.builder()
                .bucket(fileProperties.minio.bucketName)
                .`object`(objectName)
                .build()
        )

        fileInfoRepository.deleteById(fileInfoDetailDTO.id)
    }
} 