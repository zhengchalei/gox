package com.zhengchalei.gox.modules.system.repository

import com.zhengchalei.gox.modules.system.entity.Role
import com.zhengchalei.gox.modules.system.entity.dto.RoleCreateDTO
import com.zhengchalei.gox.modules.system.entity.dto.RoleListDTO
import com.zhengchalei.gox.modules.system.entity.dto.RoleSpecification
import com.zhengchalei.gox.modules.system.entity.dto.RoleUpdateDTO
import com.zhengchalei.gox.modules.system.entity.id
import org.babyfish.jimmer.spring.repository.KRepository
import org.babyfish.jimmer.spring.repository.fetchSpringPage
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.babyfish.jimmer.sql.kt.ast.expression.asc
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : KRepository<Role, Long> {

    fun findPage(
            pageRequest: PageRequest,
            roleSpecification: RoleSpecification
    ): Page<RoleListDTO> {
        return this.sql
                .createQuery(Role::class) {
                    where(roleSpecification)
                    orderBy(table.id.asc())
                    select(table.fetch(RoleListDTO::class))
                }
                .fetchSpringPage(pageRequest.pageNumber, pageRequest.pageSize)
    }

    fun save(roleCreateDTO: RoleCreateDTO): Role {
        val saveResult = this.sql.save(roleCreateDTO)
        return saveResult.modifiedEntity
    }

    fun updateById(roleUpdateDTO: RoleUpdateDTO): Role {
        val updateResult = this.sql.save(roleUpdateDTO, SaveMode.UPDATE_ONLY)
        return updateResult.modifiedEntity
    }
}
