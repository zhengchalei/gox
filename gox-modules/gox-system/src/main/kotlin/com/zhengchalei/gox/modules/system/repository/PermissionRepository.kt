package com.zhengchalei.gox.modules.system.repository

import com.zhengchalei.gox.modules.system.entity.Permission
import com.zhengchalei.gox.modules.system.entity.dto.*
import com.zhengchalei.gox.modules.system.entity.id
import org.babyfish.jimmer.spring.repository.fetchSpringPage
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.asc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository

@Repository
class PermissionRepository(private val sqlClient: KSqlClient) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun findPage(
        pageRequest: PageRequest,
        permissionSpecification: PermissionSpecification
    ): Page<PermissionListDTO> {
        val query =
            this.sqlClient.createQuery(Permission::class) {
                where(permissionSpecification)
                orderBy(table.id.asc())
                select(table.fetch(PermissionListDTO::class))
            }
        return query.fetchSpringPage(pageRequest.pageNumber, pageRequest.pageSize)
    }

    fun findById(id: Long): PermissionDetailDTO {
        return this.sqlClient
            .createQuery(Permission::class) {
                where(table.id eq id)
                select(table.fetch(PermissionDetailDTO::class))
            }
            .fetchOneOrNull()
            ?: throw IllegalArgumentException("权限不存在")
    }

    fun save(permissionCreateDTO: PermissionCreateDTO): Permission {
        val saveResult = this.sqlClient.save(permissionCreateDTO)
        return saveResult.modifiedEntity
    }

    fun updateById(permissionUpdateDTO: PermissionUpdateDTO): Permission {
        val saveResult = this.sqlClient.save(permissionUpdateDTO)
        return saveResult.modifiedEntity
    }

    // delete permission
    fun deleteById(id: Long) {
        val count: Int = this.sqlClient.executeDelete(Permission::class) { where(table.id eq id) }
    }
}