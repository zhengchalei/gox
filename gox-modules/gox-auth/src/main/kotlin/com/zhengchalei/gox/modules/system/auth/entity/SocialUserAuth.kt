package com.zhengchalei.gox.modules.system.auth.entity

import com.zhengchalei.gox.modules.system.entity.User
import org.babyfish.jimmer.sql.*
import java.time.LocalDateTime

/**
 * 社会化用户 & 系统用户关系表
 */
@Entity
@Table(name = "sys_social_user_auth")
interface SocialUserAuth {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long

    /**
     * 系统用户ID
     */
    @Key
    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User

    /**
     * 社会化用户ID
     */
    @Key
    @ManyToOne
    @JoinColumn(name = "social_user_id")
    val socialUser: SocialUser

    /**
     * 创建时间
     */
    val createdTime: LocalDateTime

    /**
     * 更新时间
     */
    val updatedTime: LocalDateTime

    @IdView("user")
    val userId: Long

    @IdView("socialUser")
    val socialUserId: Long
} 