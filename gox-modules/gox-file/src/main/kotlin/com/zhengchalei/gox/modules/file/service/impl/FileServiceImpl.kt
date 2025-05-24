package com.zhengchalei.gox.modules.file.service.impl

import com.zhengchalei.gox.modules.file.config.FileProperties
import com.zhengchalei.gox.modules.file.entity.StorageType
import com.zhengchalei.gox.modules.file.entity.dto.FileInfoDetailDTO
import com.zhengchalei.gox.modules.file.entity.dto.FileInfoListDTO
import com.zhengchalei.gox.modules.file.entity.dto.FileInfoSpecification
import com.zhengchalei.gox.modules.file.repository.FileInfoRepository
import com.zhengchalei.gox.modules.file.service.FileService
import com.zhengchalei.gox.modules.file.service.FileStorageService
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.Duration

/**
 * 文件服务实现
 */
@Service
@Transactional(rollbackFor = [Exception::class])
class FileServiceImpl(
    private val fileInfoRepository: FileInfoRepository,
    private val fileProperties: FileProperties,
    private val applicationContext: ApplicationContext
) : FileService {

    private val logger = LoggerFactory.getLogger(FileServiceImpl::class.java)

    /**
     * 获取默认的文件存储服务
     */
    private fun getDefaultStorageService(): FileStorageService {
        // 根据配置的存储类型获取对应的存储服务
        val beanClass = when (fileProperties.storageType.lowercase()) {
            "local" -> LocalFileStorageServiceImpl::class.java
            "aliyun" -> AliyunOssFileStorageServiceImpl::class.java
            "tencent" -> TencentCosFileStorageServiceImpl::class.java
            "minio" -> MinioFileStorageServiceImpl::class.java
            else -> LocalFileStorageServiceImpl::class.java
        }

        return applicationContext.getBean(beanClass)
    }

    /**
     * 根据存储类型获取文件存储服务
     */
    private fun getStorageService(storageType: StorageType): FileStorageService {
        val beanClass = when (storageType) {
            StorageType.LOCAL -> LocalFileStorageServiceImpl::class.java
            StorageType.ALIYUN -> AliyunOssFileStorageServiceImpl::class.java
            StorageType.TENCENT -> TencentCosFileStorageServiceImpl::class.java
            StorageType.MINIO -> MinioFileStorageServiceImpl::class.java
        }

        return applicationContext.getBean(beanClass)
    }

    /**
     * 分页查询文件
     */
    override fun findPage(
        pageRequest: PageRequest,
        specification: FileInfoSpecification
    ): Page<FileInfoListDTO> {
        logger.info("分页查询文件，页码: {}, 每页数量: {}", pageRequest.pageNumber, pageRequest.pageSize)
        return fileInfoRepository.findPage(pageRequest, specification).also {
            logger.info("分页查询文件成功，总记录数: {}", it.totalElements)
        }
    }

    /**
     * 上传文件
     */
    override fun upload(file: MultipartFile): FileInfoDetailDTO {
        logger.info("上传文件，文件名: {}", file.originalFilename)
        val fileInfo = getDefaultStorageService().upload(file)
        val result = findById(fileInfo.id)
        logger.info("上传文件成功，文件ID: {}", result.id)
        return result
    }

    /**
     * 批量上传文件
     */
    override fun batchUpload(files: List<MultipartFile>): List<FileInfoDetailDTO> {
        logger.info("批量上传文件，文件数量: {}", files.size)
        val fileInfos = getDefaultStorageService().batchUpload(files)
        val result = fileInfoRepository.findAllById(fileInfos.map { it.id })
        logger.info("批量上传文件成功，成功上传数量: {}", result.size)
        return result
    }

    /**
     * 根据ID查询文件详情
     */
    override fun findById(id: Long): FileInfoDetailDTO {
        logger.info("查询文件详情，ID: {}", id)
        return fileInfoRepository.findById(id)
            ?: throw IllegalArgumentException("文件不存在，ID: $id")
    }

    /**
     * 根据存储名称查询文件详情
     */
    override fun findByFileKey(fileKey: String): FileInfoDetailDTO {
        logger.info("根据存储名称查询文件详情，存储名称: {}", fileKey)
        return fileInfoRepository.findByFileKey(fileKey)
            ?: throw IllegalArgumentException("文件不存在，存储名称: $fileKey")
    }

    /**
     * 根据原始名称查询文件信息
     */
    override fun findByOriginalName(originalName: String): List<FileInfoListDTO> {
        logger.info("根据原始名称查询文件，原始名称: {}", originalName)
        val result = fileInfoRepository.findByOriginalNameContaining(originalName)
        logger.info("根据原始名称查询文件成功，找到记录数: {}", result.size)
        return result
    }

    /**
     * 根据存储类型查询文件信息
     */
    override fun findByStorageType(storageType: StorageType): List<FileInfoListDTO> {
        logger.info("根据存储类型查询文件，存储类型: {}", storageType)
        val result = fileInfoRepository.findByStorageType(storageType)
        logger.info("根据存储类型查询文件成功，找到记录数: {}", result.size)
        return result
    }

    /**
     * 根据存储类型和原始名称查询文件信息
     */
    override fun findByStorageTypeAndOriginalName(
        storageType: StorageType,
        originalName: String
    ): List<FileInfoListDTO> {
        logger.info("根据存储类型和原始名称查询文件，存储类型: {}, 原始名称: {}", storageType, originalName)
        val result = fileInfoRepository.findByStorageTypeAndOriginalNameContaining(storageType, originalName)
        logger.info("根据存储类型和原始名称查询文件成功，找到记录数: {}", result.size)
        return result
    }

    /**
     * 获取文件下载URL
     */
    override fun getDownloadUrl(fileInfo: FileInfoDetailDTO): String {
        logger.info("获取文件下载URL，文件ID: {}", fileInfo.id)
        val url = getStorageService(fileInfo.storageType).getDownloadUrl(fileInfo)
        logger.info("获取文件下载URL成功，文件ID: {}", fileInfo.id)
        return url
    }

    /**
     * 获取文件临时访问URL
     */
    override fun getTemporaryUrl(fileInfo: FileInfoDetailDTO, duration: Duration): String {
        logger.info("获取文件临时访问URL，文件ID: {}, 有效期: {}", fileInfo.id, duration)
        val url = getStorageService(fileInfo.storageType).getTemporaryUrl(fileInfo, duration)
        logger.info("获取文件临时访问URL成功，文件ID: {}", fileInfo.id)
        return url
    }

    /**
     * 删除文件
     */
    override fun delete(id: Long): Boolean {
        logger.info("删除文件，ID: {}", id)
        val fileInfo = try {
            findById(id)
        } catch (e: IllegalArgumentException) {
            logger.warn("删除文件失败，文件不存在，ID: {}", id)
            return false
        }

        getStorageService(fileInfo.storageType).delete(fileInfo)
        val result = fileInfoRepository.deleteById(id)
        if (result) {
            logger.info("删除文件成功，ID: {}", id)
        } else {
            logger.warn("删除文件失败，ID: {}", id)
        }
        return result
    }

    /**
     * 批量删除文件
     */
    override fun batchDelete(ids: List<Long>): Int {
        logger.info("批量删除文件，ID列表: {}", ids)
        val fileInfos = fileInfoRepository.findAllById(ids)
        if (fileInfos.isEmpty()) {
            logger.warn("批量删除文件失败，未找到任何文件")
            return 0
        }

        // 按存储类型分组，批量删除
        fileInfos.groupBy { it.storageType }
            .forEach { (storageType, files) ->
                getStorageService(storageType).batchDelete(files)
            }

        var deleteCount = 0
        for (id in ids) {
            if (fileInfoRepository.deleteById(id)) {
                deleteCount++
            }
        }

        logger.info("批量删除文件成功，成功删除数量: {}", deleteCount)
        return deleteCount
    }
} 