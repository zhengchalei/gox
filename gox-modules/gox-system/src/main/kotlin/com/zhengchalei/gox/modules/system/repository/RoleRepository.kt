package com.zhengchalei.gox.modules.system.repository

import com.zhengchalei.gox.modules.system.entity.Role
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
class RoleRepository(private val sqlClient: KSqlClient) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun findPage(
        pageRequest: PageRequest,
        roleSpecification: RoleSpecification
    ): Page<RoleListDTO> {
        val query =
            this.sqlClient.createQuery(Role::class) {
                where(roleSpecification)
                orderBy(table.id.asc())
                select(table.fetch(RoleListDTO::class))
            }
        return query.fetchSpringPage(pageRequest.pageNumber, pageRequest.pageSize)
    }

    fun findById(id: Long): RoleDetailDTO {
        return this.sqlClient
            .createQuery(Role::class) {
                where(table.id eq id)
                select(table.fetch(RoleDetailDTO::class))
            }
            .fetchOneOrNull()
            ?: throw IllegalArgumentException("角色不存在")
    }

    fun save(roleCreateDTO: RoleCreateDTO): Role {
        val saveResult = this.sqlClient.save(roleCreateDTO)
        if (!saveResult.isModified) {
            log.error("创建失败: {}", roleCreateDTO)
            throw IllegalArgumentException("创建失败")
        }
        return saveResult.modifiedEntity
    }

    fun updateById(roleUpdateDTO: RoleUpdateDTO): Role {
        val saveResult = this.sqlClient.save(roleUpdateDTO)
        if (!saveResult.isModified) {
            log.error("更新失败: {}", roleUpdateDTO.name)
            throw IllegalArgumentException("更新失败")
        }
        return saveResult.modifiedEntity
    }

    // delete role
    fun deleteById(id: Long) {
        val deleteCount = this.sqlClient.executeDelete(Role::class) { where(table.id eq id) }
        if (deleteCount == 0) {
            log.error("删除失败: {}", id)
            throw IllegalArgumentException("删除失败")
        }
    }
}