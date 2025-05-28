package com.zhengchalei.gox.modules.system.repository

import com.zhengchalei.gox.modules.system.entity.Permission
import com.zhengchalei.gox.modules.system.entity.dto.PermissionCreateDTO
import com.zhengchalei.gox.modules.system.entity.dto.PermissionListDTO
import com.zhengchalei.gox.modules.system.entity.dto.PermissionSpecification
import com.zhengchalei.gox.modules.system.entity.dto.PermissionUpdateDTO
import com.zhengchalei.gox.modules.system.entity.id
import org.babyfish.jimmer.spring.repository.KRepository
import org.babyfish.jimmer.spring.repository.fetchSpringPage
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
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

    fun save(permissionCreateDTO: PermissionCreateDTO): Permission {
        val saveResult = this.sql.save(permissionCreateDTO)
        return saveResult.modifiedEntity
    }

    fun updateById(permissionUpdateDTO: PermissionUpdateDTO): Permission {
        val saveResult = this.sql.save(permissionUpdateDTO, SaveMode.UPDATE_ONLY)
        return saveResult.modifiedEntity
    }
}
