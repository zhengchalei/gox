package com.zhengchalei.gox.modules.file.service

import com.zhengchalei.gox.modules.file.entity.StorageType
import com.zhengchalei.gox.modules.file.entity.dto.FileInfoDetailDTO
import com.zhengchalei.gox.modules.file.entity.dto.FileInfoListDTO
import com.zhengchalei.gox.modules.file.entity.dto.FileInfoSpecification
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.web.multipart.MultipartFile
import java.time.Duration

/**
 * 文件服务接口
 */
interface FileService {

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件详情DTO
     */
    fun upload(file: MultipartFile): FileInfoDetailDTO

    /**
     * 批量上传文件
     *
     * @param files 文件列表
     * @return 文件详情DTO列表
     */
    fun batchUpload(files: List<MultipartFile>): List<FileInfoDetailDTO>

    /**
     * 分页查询文件
     *
     * @param pageRequest 分页请求
     * @param specification 查询条件
     * @return 分页结果
     */
    fun findPage(
        pageRequest: PageRequest,
        specification: FileInfoSpecification
    ): Page<FileInfoListDTO>

    /**
     * 根据ID查询文件详情
     *
     * @param id 文件ID
     * @return 文件详情DTO
     */
    fun findById(id: Long): FileInfoDetailDTO

    /**
     * 根据存储名称查询文件详情
     *
     * @param fileKey 存储名称
     * @return 文件详情DTO
     */
    fun findByFileKey(fileKey: String): FileInfoDetailDTO

    /**
     * 根据原始名称查询文件信息
     *
     * @param originalName 原始名称（支持模糊查询）
     * @return 文件信息列表
     */
    fun findByOriginalName(originalName: String): List<FileInfoListDTO>

    /**
     * 根据存储类型查询文件信息
     *
     * @param storageType 存储类型
     * @return 文件信息列表
     */
    fun findByStorageType(storageType: StorageType): List<FileInfoListDTO>

    /**
     * 根据存储类型和原始名称查询文件信息
     *
     * @param storageType 存储类型
     * @param originalName 原始名称（支持模糊查询）
     * @return 文件信息列表
     */
    fun findByStorageTypeAndOriginalName(storageType: StorageType, originalName: String): List<FileInfoListDTO>

    /**
     * 获取文件下载URL
     *
     * @param fileInfo 文件信息
     * @return 下载URL
     */
    fun getDownloadUrl(fileInfo: FileInfoDetailDTO): String

    /**
     * 获取文件临时访问URL
     *
     * @param fileInfo 文件信息
     * @param duration 有效期
     * @return 临时访问URL
     */
    fun getTemporaryUrl(fileInfo: FileInfoDetailDTO, duration: Duration): String

    /**
     * 删除文件
     *
     * @param id 文件ID
     * @return 是否删除成功
     */
    fun delete(id: Long): Boolean

    /**
     * 批量删除文件
     *
     * @param ids 文件ID列表
     * @return 成功删除的文件数
     */
    fun batchDelete(ids: List<Long>): Int
} 