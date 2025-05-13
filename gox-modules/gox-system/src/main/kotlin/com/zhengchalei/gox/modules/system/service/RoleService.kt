package com.zhengchalei.gox.modules.system.service

import com.zhengchalei.gox.modules.system.entity.dto.*
import com.zhengchalei.gox.modules.system.repository.RoleRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(rollbackFor = [Exception::class])
class RoleService(
    private val roleRepository: RoleRepository
) {

    private val logger = org.slf4j.LoggerFactory.getLogger(RoleService::class.java)

    /**
     * 根据 id 查询角色
     */
    fun findById(id: Long): RoleDetailDTO {
        logger.info("查询角色，ID: {}", id)
        return roleRepository.findById(id).also {
            logger.info("查询角色成功，ID: {}", id)
        }
    }

    /**
     * 根据 id 删除角色
     */
    fun deleteById(id: Long) {
        logger.info("删除角色，ID: {}", id)
        roleRepository.deleteById(id)
        logger.info("删除角色成功，ID: {}", id)
    }

    /**
     * 创建角色
     */
    fun create(roleCreateDTO: RoleCreateDTO) {
        logger.info("创建角色，名称: {}", roleCreateDTO.name)
        roleRepository.save(roleCreateDTO)
        logger.info("创建角色成功，名称: {}", roleCreateDTO.name)
    }

    /**
     * 更新角色
     */
    fun update(roleUpdateDTO: RoleUpdateDTO) {
        logger.info("更新角色，名称: {}", roleUpdateDTO.name)
        roleRepository.updateById(roleUpdateDTO)
        logger.info("更新角色成功，名称: {}", roleUpdateDTO.name)
    }

    /**
     * 分页查询角色
     */
    fun findPage(pageRequest: PageRequest, roleSpecification: RoleSpecification): Page<RoleListDTO> {
        logger.info("分页查询角色，页码: {}, 每页数量: {}", pageRequest.pageNumber, pageRequest.pageSize)
        return roleRepository.findPage(pageRequest, roleSpecification).also {
            logger.info("分页查询角色成功，总记录数: {}", it.totalElements)
        }
    }
}