package com.zhengchalei.gox.modules.file.service.impl

import com.zhengchalei.gox.modules.file.config.FileProperties
import com.zhengchalei.gox.modules.file.entity.FileInfo
import com.zhengchalei.gox.modules.file.entity.StorageType
import com.zhengchalei.gox.modules.file.entity.dto.FileInfoDetailDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.time.Duration

/**
 * 本地文件存储服务实现
 */
@Service
class LocalFileStorageServiceImpl : AbstractFileStorageService() {

    @Autowired
    private lateinit var fileProperties: FileProperties

    /**
     * 获取存储类型
     */
    override fun getStorageType(): StorageType = StorageType.LOCAL

    /**
     * 上传文件
     */
    override fun upload(file: MultipartFile): FileInfo {
        val originalFilename = file.originalFilename ?: "unknown"
        val extension = getExtension(originalFilename)
        val path = generatePath()
        val storageName = generateStorageName(extension)

        // 创建目录
        val dirPath = Paths.get(fileProperties.local.basePath, path)
        Files.createDirectories(dirPath)

        // 保存文件
        val filePath = dirPath.resolve(storageName)
        Files.copy(file.inputStream, filePath, StandardCopyOption.REPLACE_EXISTING)

        // 保存文件信息
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

        // 创建目录
        val dirPath = Paths.get(fileProperties.local.basePath, path)
        Files.createDirectories(dirPath)

        // 保存文件
        val filePath = dirPath.resolve(storageName)
        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING)

        // 保存文件信息
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
        return ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/api/file/download/")
            .path(fileInfoDetailDTO.storageName)
            .toUriString()
    }

    /**
     * 获取文件临时访问URL
     * 注意：本地文件存储不支持真正的临时URL，这里返回的是普通下载URL
     */
    override fun getTemporaryUrl(fileInfoDetailDTO: FileInfoDetailDTO, duration: Duration): String {
        // 本地存储方式不支持真正的临时URL，使用普通下载URL加token参数模拟
        val token = generateTemporaryToken(fileInfoDetailDTO, duration)
        return ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/api/file/temp/")
            .path(fileInfoDetailDTO.storageName)
            .queryParam("token", token)
            .queryParam("expires", System.currentTimeMillis() + duration.toMillis())
            .toUriString()
    }

    /**
     * 删除文件
     */
    override fun delete(fileInfoDetailDTO: FileInfoDetailDTO) {
        val filePath = Paths.get(fileProperties.local.basePath, fileInfoDetailDTO.path, fileInfoDetailDTO.storageName)
        Files.deleteIfExists(filePath)
    }

    /**
     * 获取文件物理路径
     */
    fun getPhysicalPath(fileInfoDetailDTO: FileInfoDetailDTO): Path {
        return Paths.get(fileProperties.local.basePath, fileInfoDetailDTO.path, fileInfoDetailDTO.storageName)
    }

    /**
     * 生成临时访问令牌（简单实现）
     */
    private fun generateTemporaryToken(fileInfoDetailDTO: FileInfoDetailDTO, duration: Duration): String {
        // 简单实现，实际项目中应该使用更安全的方式
        val expiresAt = System.currentTimeMillis() + duration.toMillis()
        return "${fileInfoDetailDTO.id}_${fileInfoDetailDTO.storageName}_${expiresAt}"
    }
} 