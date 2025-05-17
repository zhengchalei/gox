package com.zhengchalei.gox.modules.system.repository

import com.zhengchalei.gox.modules.system.entity.RoutePermission
import com.zhengchalei.gox.modules.system.entity.dto.*
import com.zhengchalei.gox.modules.system.entity.id
import com.zhengchalei.gox.modules.system.entity.path
import com.zhengchalei.gox.modules.system.entity.method
import org.babyfish.jimmer.spring.repository.fetchSpringPage
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.asc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository

@Repository
class RoutePermissionRepository(private val sqlClient: KSqlClient) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun findPage(
        pageRequest: PageRequest,
        routePermissionSpecification: RoutePermissionSpecification
    ): Page<RoutePermissionListDTO> {
        val query =
            this.sqlClient.createQuery(RoutePermission::class) {
                where(routePermissionSpecification)
                orderBy(table.id.asc())
                select(table.fetch(RoutePermissionListDTO::class))
            }
        return query.fetchSpringPage(pageRequest.pageNumber, pageRequest.pageSize)
    }
    
    fun findByPathAndMethod(path: String, method: String): RoutePermissionDetailDTO? {
        return this.sqlClient
            .createQuery(RoutePermission::class) {
                where(
                    table.path eq path,
                    table.method eq method
                )
                select(table.fetch(RoutePermissionDetailDTO::class))
            }
            .fetchOneOrNull()
    }

    fun findById(id: Long): RoutePermissionDetailDTO {
        return this.sqlClient
            .createQuery(RoutePermission::class) {
                where(table.id eq id)
                select(table.fetch(RoutePermissionDetailDTO::class))
            }
            .fetchOneOrNull()
            ?: throw IllegalArgumentException("路由权限不存在")
    }

    fun save(routePermissionCreateDTO: RoutePermissionCreateDTO): RoutePermission {
        val saveResult = this.sqlClient.save(routePermissionCreateDTO)
        if (!saveResult.isModified) {
            log.error("创建失败: {}", routePermissionCreateDTO)
            throw IllegalArgumentException("创建失败")
        }
        return saveResult.modifiedEntity
    }

    fun updateById(routePermissionUpdateDTO: RoutePermissionUpdateDTO): RoutePermission {
        val saveResult = this.sqlClient.save(routePermissionUpdateDTO)
        if (!saveResult.isModified) {
            log.error("更新失败: {}", routePermissionUpdateDTO.path)
            throw IllegalArgumentException("更新失败")
        }
        return saveResult.modifiedEntity
    }

    fun deleteById(id: Long) {
        val count: Int = this.sqlClient.executeDelete(RoutePermission::class) { where(table.id eq id) }
        if (count == 0) {
            log.error("删除失败: {}", id)
            throw IllegalArgumentException("删除失败")
        }
    }
    
    /**
     * 获取用户角色对应的所有路由权限
     */
    fun findByRoleIds(roleIds: List<Long>): List<RoutePermissionDetailDTO> {
        if (roleIds.isEmpty()) {
            return emptyList()
        }
        
        return this.sqlClient
            .createQuery(RoutePermission::class) {
                where(
                    table.roles.id valueIn roleIds
                )
                select(table.fetch(RoutePermissionDetailDTO::class))
            }
            .execute()
    }
} 