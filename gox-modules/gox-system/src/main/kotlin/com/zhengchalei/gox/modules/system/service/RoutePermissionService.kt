package com.zhengchalei.gox.modules.system.service

import com.zhengchalei.gox.modules.system.entity.dto.*
import com.zhengchalei.gox.modules.system.repository.RoutePermissionRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(rollbackFor = [Exception::class])
class RoutePermissionService(
    private val routePermissionRepository: RoutePermissionRepository
) {

    private val logger = org.slf4j.LoggerFactory.getLogger(RoutePermissionService::class.java)

    /**
     * 分页查询路由权限
     */
    fun findPage(pageRequest: PageRequest, routePermissionSpecification: RoutePermissionSpecification): Page<RoutePermissionListDTO> {
        logger.info("分页查询路由权限")
        return routePermissionRepository.findPage(pageRequest, routePermissionSpecification).also {
            logger.info("分页查询路由权限成功，总数: {}", it.totalElements)
        }
    }

    /**
     * 根据路径和方法查询路由权限
     */
    fun findByPathAndMethod(path: String, method: String): RoutePermissionDetailDTO? {
        logger.info("查询路由权限，路径: {}, 方法: {}", path, method)
        return routePermissionRepository.findByPathAndMethod(path, method).also {
            logger.info("查询路由权限完成，路径: {}, 方法: {}", path, method)
        }
    }

    /**
     * 根据 id 查询路由权限
     */
    fun findById(id: Long): RoutePermissionDetailDTO {
        logger.info("查询路由权限，ID: {}", id)
        return routePermissionRepository.findById(id).also {
            logger.info("查询路由权限成功，ID: {}", id)
        }
    }

    /**
     * 根据 id 删除路由权限
     */
    fun deleteById(id: Long) {
        logger.info("删除路由权限，ID: {}", id)
        routePermissionRepository.deleteById(id)
        logger.info("删除路由权限成功，ID: {}", id)
    }

    /**
     * 创建路由权限
     */
    fun create(routePermissionCreateDTO: RoutePermissionCreateDTO) {
        logger.info("创建路由权限，路径: {}", routePermissionCreateDTO.path)
        routePermissionRepository.save(routePermissionCreateDTO)
        logger.info("创建路由权限成功，路径: {}", routePermissionCreateDTO.path)
    }

    /**
     * 更新路由权限
     */
    fun update(routePermissionUpdateDTO: RoutePermissionUpdateDTO) {
        logger.info("更新路由权限，ID: {}", routePermissionUpdateDTO.id)
        routePermissionRepository.updateById(routePermissionUpdateDTO)
        logger.info("更新路由权限成功，ID: {}", routePermissionUpdateDTO.id)
    }
    
    /**
     * 根据角色ID列表查询路由权限
     */
    fun findByRoleIds(roleIds: List<Long>): List<RoutePermissionDetailDTO> {
        logger.info("根据角色ID查询路由权限，角色ID: {}", roleIds)
        return routePermissionRepository.findByRoleIds(roleIds).also {
            logger.info("根据角色ID查询路由权限成功，数量: {}", it.size)
        }
    }
} 