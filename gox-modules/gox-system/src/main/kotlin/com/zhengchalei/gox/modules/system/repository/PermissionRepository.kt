package com.zhengchalei.gox.modules.system.repository

import com.zhengchalei.gox.modules.system.entity.Permission
import com.zhengchalei.gox.modules.system.entity.dto.PermissionListDTO
import com.zhengchalei.gox.modules.system.entity.dto.PermissionSpecification
import com.zhengchalei.gox.modules.system.entity.id
import org.babyfish.jimmer.spring.repository.KRepository
import org.babyfish.jimmer.spring.repository.fetchSpringPage
import org.babyfish.jimmer.sql.kt.ast.expression.asc
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository

@Repository
interface PermissionRepository : KRepository<Permission, Long> {

    fun findPage(
        pageRequest: PageRequest,
        permissionSpecification: PermissionSpecification
    ): Page<PermissionListDTO> {
        return this.sql
            .createQuery(Permission::class) {
                where(permissionSpecification)
                orderBy(table.id.asc())
                select(table.fetch(PermissionListDTO::class))
            }
            .fetchSpringPage(pageRequest.pageNumber, pageRequest.pageSize)
    }

}
