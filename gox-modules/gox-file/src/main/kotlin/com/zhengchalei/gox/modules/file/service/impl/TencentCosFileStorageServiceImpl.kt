package com.zhengchalei.gox.modules.file.service.impl

import com.qcloud.cos.COSClient
import com.qcloud.cos.ClientConfig
import com.qcloud.cos.auth.BasicCOSCredentials
import com.qcloud.cos.auth.COSCredentials
import com.qcloud.cos.http.HttpProtocol
import com.qcloud.cos.model.ObjectMetadata
import com.qcloud.cos.model.PutObjectRequest
import com.qcloud.cos.region.Region
import com.zhengchalei.gox.modules.file.config.FileProperties
import com.zhengchalei.gox.modules.file.entity.FileInfo
import com.zhengchalei.gox.modules.file.entity.StorageType
import com.zhengchalei.gox.modules.file.entity.dto.FileInfoDetailDTO
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream
import java.net.URL
import java.time.Duration
import java.util.*

/**
 * 腾讯云COS文件存储服务实现
 */
@Service
@ConditionalOnProperty(prefix = "gox.file", name = ["storageType"], havingValue = "tencent")
class TencentCosFileStorageServiceImpl : AbstractFileStorageService() {

    @Autowired
    private lateinit var fileProperties: FileProperties

    private lateinit var cosClient: COSClient

    @PostConstruct
    fun init() {
        // 初始化腾讯云COS客户端
        val credentials: COSCredentials = BasicCOSCredentials(
            fileProperties.tencent.secretId, fileProperties.tencent.secretKey
        )
        val region = Region(fileProperties.tencent.region)
        val clientConfig = ClientConfig(region)

        // 设置使用HTTPS协议
        clientConfig.httpProtocol = HttpProtocol.https

        cosClient = COSClient(credentials, clientConfig)

        // 检查存储桶是否存在（腾讯云COS需要提前创建好存储桶）
        if (!cosClient.doesBucketExist(fileProperties.tencent.bucketName)) {
            throw IllegalStateException("存储桶 ${fileProperties.tencent.bucketName} 不存在，请先在腾讯云控制台创建")
        }
    }

    @PreDestroy
    fun destroy() {
        cosClient.shutdown()
    }

    /**
     * 获取存储类型
     */
    override fun getStorageType(): StorageType = StorageType.TENCENT

    /**
     * 上传文件
     */
    override fun upload(file: MultipartFile): FileInfo {
        val originalFilename = file.originalFilename ?: "unknown"
        val extension = getExtension(originalFilename)
        val path = generatePath()
        val storageName = generateStorageName(extension)
        val key = path + storageName

        // 设置元数据
        val metadata = ObjectMetadata()
        metadata.contentLength = file.size
        metadata.contentType = file.contentType ?: "application/octet-stream"

        // 创建上传请求
        val putObjectRequest = PutObjectRequest(
            fileProperties.tencent.bucketName, key, file.inputStream, metadata
        )

        // 上传文件
        cosClient.putObject(putObjectRequest)

        return createFileInfo(
            originalFilename = originalFilename,
            storageName = storageName,
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
        val storageName = generateStorageName(extension)
        val key = path + storageName

        // 设置元数据
        val metadata = ObjectMetadata()
        metadata.contentLength = size
        metadata.contentType = mimeType

        // 创建上传请求
        val putObjectRequest = PutObjectRequest(
            fileProperties.tencent.bucketName, key, inputStream, metadata
        )

        // 上传文件
        cosClient.putObject(putObjectRequest)

        return createFileInfo(
            originalFilename = originalFilename,
            storageName = storageName,
            path = path,
            size = size,
            mimeType = mimeType
        )
    }

    /**
     * 获取文件下载URL
     */
    override fun getDownloadUrl(fileInfoDetailDTO: FileInfoDetailDTO): String {
        if (fileProperties.tencent.domain != null) {
            return "${fileProperties.tencent.domain}/${fileInfoDetailDTO.path}${fileInfoDetailDTO.storageName}"
        }

        return "https://${fileProperties.tencent.bucketName}.cos.${fileProperties.tencent.region}.myqcloud.com/${fileInfoDetailDTO.path}${fileInfoDetailDTO.storageName}"
    }

    /**
     * 获取文件临时访问URL
     */
    override fun getTemporaryUrl(fileInfoDetailDTO: FileInfoDetailDTO, duration: Duration): String {
        val key = fileInfoDetailDTO.path + fileInfoDetailDTO.storageName

        // 计算过期时间（秒）
        val expirationInSeconds = duration.seconds

        // 生成临时URL
        val expiration = Date()
        val expirationMillis = expiration.time + expirationInSeconds * 1000
        expiration.time = expirationMillis

        val url: URL = cosClient.generatePresignedUrl(
            fileProperties.tencent.bucketName, key, expiration
        )

        return url.toString()
    }

    /**
     * 删除文件
     */
    override fun delete(fileInfoDetailDTO: FileInfoDetailDTO) {
        val key = fileInfoDetailDTO.path + fileInfoDetailDTO.storageName
        cosClient.deleteObject(fileProperties.tencent.bucketName, key)
        fileInfoRepository.deleteById(fileInfoDetailDTO.id)
    }
} 