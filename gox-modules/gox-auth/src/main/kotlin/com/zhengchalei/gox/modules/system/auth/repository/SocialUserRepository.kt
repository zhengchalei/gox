package com.zhengchalei.gox.modules.system.auth.repository

import com.zhengchalei.gox.modules.system.auth.dto.SocialUserDetailDTO
import com.zhengchalei.gox.modules.system.auth.entity.SocialUser
import com.zhengchalei.gox.modules.system.auth.entity.id
import com.zhengchalei.gox.modules.system.auth.entity.source
import com.zhengchalei.gox.modules.system.auth.entity.uuid
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.valueIn
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

/**
 * 社会化用户仓库
 */
@Repository
class SocialUserRepository(private val sqlClient: KSqlClient) {

    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * 根据UUID和来源查询社会化用户
     */
    fun findByUuidAndSource(uuid: String, source: String): SocialUserDetailDTO? {
        return this.sqlClient
            .createQuery(SocialUser::class) {
                where(table.uuid eq uuid)
                where(table.source eq source)
                select(table.fetch(SocialUserDetailDTO::class))
            }
            .fetchOneOrNull()
    }

    /**
     * 根据ID查询社会化用户
     */
    fun findById(id: Long): SocialUserDetailDTO? {
        return this.sqlClient
            .createQuery(SocialUser::class) {
                where(table.id eq id)
                select(table.fetch(SocialUserDetailDTO::class))
            }
            .fetchOneOrNull()
    }

    /**
     * 批量查询社会化用户
     */
    fun findAllById(ids: List<Long>): List<SocialUserDetailDTO> {
        return this.sqlClient
            .createQuery(SocialUser::class) {
                where(table.id valueIn ids)
                select(table.fetch(SocialUserDetailDTO::class))
            }
            .execute()
    }

    /**
     * 保存社会化用户
     */
    fun save(socialUser: SocialUser): SocialUser {
        val saveResult = this.sqlClient.save(socialUser)
        if (!saveResult.isModified) {
            log.error("社会化用户保存失败: {}", socialUser.uuid)
            throw IllegalArgumentException("社会化用户保存失败")
        }
        return saveResult.modifiedEntity
    }

    /**
     * 删除社会化用户
     */
    fun deleteById(id: Long): Boolean {
        val deleteCount = this.sqlClient.executeDelete(SocialUser::class) {
            where(table.id eq id)
        }
        return deleteCount > 0
    }
} 