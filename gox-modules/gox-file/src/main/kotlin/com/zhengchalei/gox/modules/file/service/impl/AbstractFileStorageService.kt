package com.zhengchalei.gox.modules.file.service.impl

import com.zhengchalei.gox.modules.file.entity.FileInfo
import com.zhengchalei.gox.modules.file.entity.dto.FileInfoCreateDTO
import com.zhengchalei.gox.modules.file.entity.dto.FileInfoDetailDTO
import com.zhengchalei.gox.modules.file.repository.FileInfoRepository
import com.zhengchalei.gox.modules.file.service.FileStorageService
import org.apache.commons.io.FilenameUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * 抽象文件存储服务实现
 */
abstract class AbstractFileStorageService : FileStorageService {

    @Autowired
    protected lateinit var fileInfoRepository: FileInfoRepository

    /**
     * 生成存储路径
     * 按日期生成路径：yyyy/MM/dd/
     */
    protected fun generatePath(): String {
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
        return formatter.format(now) + "/"
    }

    /**
     * 生成存储文件名（UUID）
     */
    protected fun generateStorageName(extension: String): String {
        return UUID.randomUUID().toString().replace("-", "") + "." + extension
    }

    /**
     * 获取文件扩展名
     */
    protected fun getExtension(filename: String): String {
        return FilenameUtils.getExtension(filename)
    }

    /**
     * 批量上传文件
     */
    override fun batchUpload(files: List<MultipartFile>): List<FileInfo> {
        return files.map { upload(it) }
    }

    /**
     * 批量删除文件
     */
    override fun batchDelete(fileInfos: List<FileInfoDetailDTO>) {
        fileInfos.forEach { delete(it) }
    }

    /**
     * 创建文件信息实体
     */
    protected fun createFileInfo(
        originalFilename: String,
        storageName: String,
        path: String,
        size: Long,
        mimeType: String
    ): FileInfo {
        val extension = getExtension(originalFilename)
        val fileInfoCreateDTO = FileInfoCreateDTO(
            originalName = originalFilename,
            storageName = storageName,
            path = path,
            extension = extension,
            size = size,
            mimeType = mimeType,
            storageType = getStorageType(),
        )
        // 保存到数据库
        return fileInfoRepository.save(fileInfoCreateDTO)
    }
} 