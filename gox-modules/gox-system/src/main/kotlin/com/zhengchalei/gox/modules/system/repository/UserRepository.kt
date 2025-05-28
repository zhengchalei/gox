package com.zhengchalei.gox.modules.system.repository

import com.zhengchalei.gox.modules.system.entity.User
import com.zhengchalei.gox.modules.system.entity.dto.*
import com.zhengchalei.gox.modules.system.entity.id
import com.zhengchalei.gox.modules.system.entity.username
import org.babyfish.jimmer.spring.repository.KRepository
import org.babyfish.jimmer.spring.repository.fetchSpringPage
import org.babyfish.jimmer.sql.kt.ast.expression.asc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : KRepository<User, Long> {

    fun findPage(
        pageRequest: PageRequest,
        userSpecification: UserSpecification
    ): Page<UserListDTO> {
        return this.sql
            .createQuery(User::class) {
                where(userSpecification)
                orderBy(table.id.asc())
                select(table.fetch(UserListDTO::class))
            }
            .fetchSpringPage(pageRequest.pageNumber, pageRequest.pageSize)
    }

    fun findByUsername(username: String): UserDetailDTO {
        return this.sql
            .createQuery(User::class) {
                where(table.username eq username)
                select(table.fetch(UserDetailDTO::class))
            }
            .fetchOneOrNull()
            ?: throw IllegalArgumentException("用户不存在")
    }

}
