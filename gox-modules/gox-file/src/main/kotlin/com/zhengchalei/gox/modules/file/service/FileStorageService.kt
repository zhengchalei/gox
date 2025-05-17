package com.zhengchalei.gox.modules.file.service

import com.zhengchalei.gox.modules.file.entity.FileInfo
import com.zhengchalei.gox.modules.file.entity.StorageType
import com.zhengchalei.gox.modules.file.entity.dto.FileInfoDetailDTO
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream
import java.time.Duration

/**
 * 文件存储服务接口
 */
interface FileStorageService {

    /**
     * 获取存储类型
     */
    fun getStorageType(): StorageType

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件信息
     */
    fun upload(file: MultipartFile): FileInfo

    /**
     * 批量上传文件
     *
     * @param files 文件列表
     * @return 文件信息列表
     */
    fun batchUpload(files: List<MultipartFile>): List<FileInfo>

    /**
     * 上传文件流
     *
     * @param inputStream 文件流
     * @param originalFilename 原始文件名
     * @param size 文件大小
     * @param mimeType 文件类型
     * @return 文件信息
     */
    fun upload(inputStream: InputStream, originalFilename: String, size: Long, mimeType: String): FileInfo

    /**
     * 获取文件下载URL
     *
     * @param fileInfoDetailDTO 文件信息
     * @return 下载URL
     */
    fun getDownloadUrl(fileInfoDetailDTO: FileInfoDetailDTO): String

    /**
     * 获取文件临时访问URL
     *
     * @param fileInfoDetailDTO 文件信息
     * @param duration 有效期
     * @return 临时访问URL
     */
    fun getTemporaryUrl(fileInfoDetailDTO: FileInfoDetailDTO, duration: Duration): String

    /**
     * 删除文件
     *
     * @param fileInfoDetailDTO 文件信息
     */
    fun delete(fileInfoDetailDTO: FileInfoDetailDTO)

    /**
     * 批量删除文件
     *
     * @param fileInfos 文件信息列表
     */
    fun batchDelete(fileInfos: List<FileInfoDetailDTO>)
} 