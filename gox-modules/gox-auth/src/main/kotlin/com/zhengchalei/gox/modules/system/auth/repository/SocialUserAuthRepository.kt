package com.zhengchalei.gox.modules.system.auth.repository

import com.zhengchalei.gox.modules.system.auth.entity.SocialUserAuth
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

/**
 * 社会化用户关系仓库
 */
@Repository
class SocialUserAuthRepository(private val sqlClient: KSqlClient) {

    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * 根据用户ID和来源查询社会化用户关系
     */
    fun findByUserIdAndSource(userId: Long, source: String): SocialUserAuthDetailDTO? {
        return this.sqlClient
            .createQuery(SocialUserAuth::class) {
                where(table.user.id eq userId)
                where(table.socialUser.source eq source)
                select(table.fetch(SocialUserAuthDetailDTOFetcher.SHALLOW))
            }
            .fetchOneOrNull()
    }

    /**
     * 根据用户ID查询社会化用户关系列表
     */
    fun findByUserId(userId: Long): List<SocialUserAuthDetailDTO> {
        return this.sqlClient
            .createQuery(SocialUserAuth::class) {
                where(table.user.id eq userId)
                select(table.fetch(SocialUserAuthDetailDTOFetcher.SHALLOW))
            }
            .execute()
    }

    /**
     * 根据社会化用户ID查询社会化用户关系
     */
    fun findBySocialUserId(socialUserId: Long): SocialUserAuthDetailDTO? {
        return this.sqlClient
            .createQuery(SocialUserAuth::class) {
                where(table.socialUser.id eq socialUserId)
                select(table.fetch(SocialUserAuthDetailDTOFetcher.SHALLOW))
            }
            .fetchOneOrNull()
    }

    /**
     * 保存社会化用户关系
     */
    fun save(socialUserAuth: SocialUserAuth): SocialUserAuth {
        val saveResult = this.sqlClient.save(socialUserAuth)
        if (!saveResult.isModified) {
            log.error("社会化用户关系保存失败: {}", socialUserAuth)
            throw IllegalArgumentException("社会化用户关系保存失败")
        }
        return saveResult.modifiedEntity
    }

    /**
     * 删除社会化用户关系
     */
    fun deleteByUserIdAndSocialUserId(userId: Long, socialUserId: Long): Boolean {
        val deleteCount = this.sqlClient.executeDelete(SocialUserAuth::class) {
            where(table.user.id eq userId)
            where(table.socialUser.id eq socialUserId)
        }
        return deleteCount > 0
    }
} 