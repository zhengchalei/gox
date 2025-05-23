package com.zhengchalei.gox.modules.system.repository

import com.zhengchalei.gox.modules.system.entity.User
import com.zhengchalei.gox.modules.system.entity.dto.*
import com.zhengchalei.gox.modules.system.entity.id
import com.zhengchalei.gox.modules.system.entity.username
import com.zhengchalei.gox.util.PasswordUtil
import org.babyfish.jimmer.spring.repository.fetchSpringPage
import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.asc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository

@Repository
class UserRepository(private val sqlClient: KSqlClient) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun findPage(
        pageRequest: PageRequest,
        userSpecification: UserSpecification
    ): Page<UserListDTO> {
        val query =
            this.sqlClient.createQuery(User::class) {
                where(userSpecification)
                orderBy(table.id.asc())
                select(table.fetch(UserListDTO::class))
            }
        return query.fetchSpringPage(pageRequest.pageNumber, pageRequest.pageSize)
    }

    fun findByUsername(username: String): UserDetailDTO {
        return this.sqlClient
            .createQuery(User::class) {
                where(table.username eq username)
                select(table.fetch(UserDetailDTO::class))
            }
            .fetchOneOrNull()
            ?: throw IllegalArgumentException("用户不存在")
    }

    fun findById(id: Long): UserDetailDTO {
        return this.sqlClient
            .createQuery(User::class) {
                where(table.id eq id)
                select(table.fetch(UserDetailDTO::class))
            }
            .fetchOneOrNull()
            ?: throw IllegalArgumentException("用户不存在")
    }

    fun save(userCreateDTO: UserCreateDTO): User {
        val saveResult = this.sqlClient.save(userCreateDTO.toEntity {
            password = PasswordUtil.defaultPassword()
        })
        return saveResult.modifiedEntity
    }

    fun save(user: User): User {
        val saveResult = this.sqlClient.save(user)
        return saveResult.modifiedEntity
    }

    fun updateById(userUpdateDTO: UserUpdateDTO): User {
        this.sqlClient.executeUpdate(User::class) {
            userUpdateDTO
        }
        val saveResult = this.sqlClient.save(userUpdateDTO, SaveMode.UPDATE_ONLY)
        return saveResult.modifiedEntity
    }

    // delete user
    fun deleteById(id: Long) {
        val count = this.sqlClient.executeDelete(User::class) { where(table.id eq id) }
    }
}
