package com.zhengchalei.gox.modules.system.auth.repository

import com.zhengchalei.gox.modules.system.auth.dto.SocialUserAuthDetailDTO
import com.zhengchalei.gox.modules.system.auth.entity.*
import com.zhengchalei.gox.modules.system.entity.id
import org.babyfish.jimmer.spring.repository.KRepository
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.springframework.stereotype.Repository

/**
 * 社会化用户关系仓库
 */
@Repository
interface SocialUserAuthRepository : KRepository<SocialUserAuth, Long> {

    /**
     * 根据用户ID和来源查询社会化用户关系
     */
    fun findByUserIdAndSource(userId: Long, source: String): SocialUserAuthDetailDTO? {
        return this.sql
            .createQuery(SocialUserAuth::class) {
                where(table.user.id eq userId)
                where(table.socialUser.source eq source)
                select(table.fetch(SocialUserAuthDetailDTO::class))
            }
            .fetchOneOrNull()
    }

    /**
     * 根据用户ID查询社会化用户关系列表
     */
    fun findByUserId(userId: Long): List<SocialUserAuthDetailDTO> {
        return this.sql
            .createQuery(SocialUserAuth::class) {
                where(table.user.id eq userId)
                select(table.fetch(SocialUserAuthDetailDTO::class))
            }
            .execute()
    }

    /**
     * 根据社会化用户ID查询社会化用户关系
     */
    fun findBySocialUserId(socialUserId: Long): SocialUserAuthDetailDTO? {
        return this.sql
            .createQuery(SocialUserAuth::class) {
                where(table.socialUser.id eq socialUserId)
                select(table.fetch(SocialUserAuthDetailDTO::class))
            }
            .fetchOneOrNull()
    }

    /**
     * 保存社会化用户关系
     */
    fun save(socialUserAuth: SocialUserAuth): SocialUserAuth {
        val saveResult = this.sql.save(socialUserAuth)
        return saveResult.modifiedEntity
    }

    /**
     * 删除社会化用户关系
     */
    fun deleteByUserIdAndSocialUserId(userId: Long, socialUserId: Long): Boolean {
        val deleteCount = this.sql.executeDelete(SocialUserAuth::class) {
            where(table.user.id eq userId)
            where(table.socialUser.id eq socialUserId)
        }
        return deleteCount > 0
    }
} 