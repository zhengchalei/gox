package com.zhengchalei.gox.modules.system.repository

import com.zhengchalei.gox.modules.system.entity.User
import com.zhengchalei.gox.modules.system.entity.dto.UserCreateDTO
import com.zhengchalei.gox.modules.system.entity.dto.UserUpdateDTO
import com.zhengchalei.gox.modules.system.entity.enabled
import com.zhengchalei.gox.modules.system.entity.id
import com.zhengchalei.gox.modules.system.entity.username
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.count
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

@Repository
class UserRepository(private val sqlClient: KSqlClient) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun findByUsername(username: String): User? {
        val user: User =
            this.sqlClient
                .createQuery(User::class) {
                    where(table.username eq username)
                    select(table)
                }
                .fetchOneOrNull()
                ?: throw IllegalArgumentException("用户不存在")
        return user
    }

    fun findById(id: Long): User? {
        val user: User =
            this.sqlClient
                .createQuery(User::class) {
                    where(table.id eq id)
                    select(table)
                }
                .fetchOneOrNull()
                ?: throw IllegalArgumentException("用户不存在")
        return user
    }

    fun create(userCreateDTO: UserCreateDTO): User {
        // 判断用户名是否存在
        val exist =
            this.sqlClient
                .createQuery(User::class) {
                    where(table.username eq userCreateDTO.username)
                    select(count(table.id))
                }
                .fetchOne()
        if (exist != 0L) {
            log.error("用户名已存在: {}", userCreateDTO)
            throw IllegalArgumentException("用户名已存在")
        }

        val saveResult = this.sqlClient.save(userCreateDTO)
        if (!saveResult.isModified) {
            log.error("创建失败: {}", userCreateDTO)
            throw IllegalArgumentException("创建失败")
        }
        return saveResult.modifiedEntity
    }

    fun update(userUpdateDTO: UserUpdateDTO): User {
        val saveResult = this.sqlClient.save(userUpdateDTO)
        if (!saveResult.isModified) {
            log.error("更新失败: {}", userUpdateDTO.username)
            throw IllegalArgumentException("更新失败")
        }
        return saveResult.modifiedEntity
    }

    // enable user
    fun enable(id: Long) {
        val exist =
            this.sqlClient
                .createQuery(User::class) {
                    where(table.id eq id)
                    select(count(table.id))
                }
                .fetchOneOrNull()
        if (exist == 0L) {
            log.error("用户不存在: {}", id)
            throw IllegalArgumentException("用户不存在")
        }
        this.sqlClient.executeUpdate(User::class) {
            where(table.id eq id)
            set(table.enabled, true)
        }
    }

    // disable user
    fun disable(id: Long) {
        val exist =
            this.sqlClient
                .createQuery(User::class) {
                    where(table.id eq id)
                    select(count(table.id))
                }
                .fetchOneOrNull()
        if (exist == 0L) {
            throw IllegalArgumentException("用户不存在")
        }
        this.sqlClient.executeUpdate(User::class) {
            where(table.id eq id)
            set(table.enabled, false)
        }
    }

    // delete user
    fun delete(id: Long) {
        this.sqlClient.executeDelete(User::class) { where(table.id eq id) }
    }
}
