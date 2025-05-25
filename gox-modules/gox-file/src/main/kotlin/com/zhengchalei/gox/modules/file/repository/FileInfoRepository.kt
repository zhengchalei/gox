package com.zhengchalei.gox.modules.file.repository

import com.zhengchalei.gox.modules.file.entity.*
import com.zhengchalei.gox.modules.file.entity.dto.FileInfoCreateDTO
import com.zhengchalei.gox.modules.file.entity.dto.FileInfoDetailDTO
import com.zhengchalei.gox.modules.file.entity.dto.FileInfoListDTO
import com.zhengchalei.gox.modules.file.entity.dto.FileInfoSpecification
import org.babyfish.jimmer.spring.repository.fetchSpringPage
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.asc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.like
import org.babyfish.jimmer.sql.kt.ast.expression.valueIn
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository

/**
 * 文件信息仓库接口
 */
@Repository
class FileInfoRepository(private val sqlClient: KSqlClient) {

    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * 分页查询文件信息
     */
    fun findPage(
        pageRequest: PageRequest,
        specification: FileInfoSpecification
    ): Page<FileInfoListDTO> {
        val query = this.sqlClient.createQuery(FileInfo::class) {
            // 应用查询条件
            where(specification)
            orderBy(table.id.asc())
            select(table.fetch(FileInfoListDTO::class))
        }
        return query.fetchSpringPage(pageRequest.pageNumber, pageRequest.pageSize)
    }

    /**
     * 根据ID查询文件详情
     */
    fun findById(id: Long): FileInfoDetailDTO? {
        return this.sqlClient
            .createQuery(FileInfo::class) {
                where(table.id eq id)
                select(table.fetch(FileInfoDetailDTO::class))
            }
            .fetchOneOrNull()
    }

    /**
     * 根据存储名称查询文件详情
     */
    fun findByFileKey(fileKey: String): FileInfoDetailDTO? {
        return this.sqlClient
            .createQuery(FileInfo::class) {
                where(table.fileKey eq fileKey)
                select(table.fetch(FileInfoDetailDTO::class))
            }
            .fetchOneOrNull()
    }

    /**
     * 根据原始名称查询文件列表
     */
    fun findByOriginalNameContaining(originalName: String): List<FileInfoListDTO> {
        return this.sqlClient
            .createQuery(FileInfo::class) {
                where(table.originalName like originalName)
                select(table.fetch(FileInfoListDTO::class))
            }
            .execute()
    }

    /**
     * 根据存储类型查询文件列表
     */
    fun findByStorageType(storageType: StorageType): List<FileInfoListDTO> {
        return this.sqlClient
            .createQuery(FileInfo::class) {
                where(table.storageType eq storageType)
                select(table.fetch(FileInfoListDTO::class))
            }
            .execute()
    }

    /**
     * 根据存储类型和原始名称查询文件列表
     */
    fun findByStorageTypeAndOriginalNameContaining(
        storageType: StorageType,
        originalName: String
    ): List<FileInfoListDTO> {
        return this.sqlClient
            .createQuery(FileInfo::class) {
                where(table.storageType eq storageType)
                where(table.originalName like originalName)
                select(table.fetch(FileInfoListDTO::class))
            }
            .execute()
    }

    /**
     * 保存文件信息
     */
    fun save(fileInfoCreateDTO: FileInfoCreateDTO): FileInfo {
        val saveResult = this.sqlClient.save(fileInfoCreateDTO.toEntity())
        if (!saveResult.isModified) {
            log.error("文件保存失败: {}", fileInfoCreateDTO.originalName)
            throw IllegalArgumentException("文件保存失败")
        }
        return saveResult.modifiedEntity
    }

    /**
     * 批量查询文件信息
     */
    fun findAllById(ids: List<Long>): List<FileInfoDetailDTO> {
        return this.sqlClient
            .createQuery(FileInfo::class) {
                where(table.id valueIn ids)
                select(table.fetch(FileInfoDetailDTO::class))
            }
            .execute()
    }

    /**
     * 删除文件信息
     */
    fun deleteById(id: Long): Boolean {
        val deleteCount = this.sqlClient.executeDelete(FileInfo::class) {
            where(table.id eq id)
        }
        return deleteCount > 0
    }
} 