package com.zhengchalei.gox.modules.system.service

import com.zhengchalei.gox.modules.system.entity.dto.*
import com.zhengchalei.gox.modules.system.repository.PermissionRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(rollbackFor = [Exception::class])
class PermissionService(
    private val permissionRepository: PermissionRepository,
) {

    private val logger = org.slf4j.LoggerFactory.getLogger(PermissionService::class.java)

    /**
     * 根据 id 查询权限
     */
    fun findById(id: Long): PermissionDetailDTO {
        logger.info("查询权限，ID: {}", id)
        return permissionRepository.sql.findById(PermissionDetailDTO::class, id)?.also {
            logger.info("查询权限成功，ID: {}", id)
        } ?: throw RuntimeException("权限不存在")
    }

    /**
     * 根据 id 删除权限
     */
    fun deleteById(id: Long) {
        logger.info("删除权限，ID: {}", id)
        permissionRepository.deleteById(id)
        logger.info("删除权限成功，ID: {}", id)
    }

    /**
     * 创建权限
     */
    fun create(permissionCreateDTO: PermissionCreateDTO) {
        logger.info("创建权限，名称: {}", permissionCreateDTO.name)
        permissionRepository.save(permissionCreateDTO)
        logger.info("创建权限成功，名称: {}", permissionCreateDTO.name)
    }

    /**
     * 更新权限
     */
    fun update(permissionUpdateDTO: PermissionUpdateDTO) {
        logger.info("更新权限，名称: {}", permissionUpdateDTO.name)
        permissionRepository.updateById(permissionUpdateDTO)
        logger.info("更新权限成功，名称: {}", permissionUpdateDTO.name)
    }

    /**
     * 分页查询权限
     */
    fun findPage(pageRequest: PageRequest, permissionSpecification: PermissionSpecification): Page<PermissionListDTO> {
        logger.info("分页查询权限，页码: {}, 每页数量: {}", pageRequest.pageNumber, pageRequest.pageSize)
        return permissionRepository.findPage(pageRequest, permissionSpecification).also {
            logger.info("分页查询权限成功，总记录数: {}", it.totalElements)
        }
    }
}