package com.zhengchalei.gox.modules.system.auth.entity

import org.babyfish.jimmer.sql.*
import java.time.LocalDateTime

/**
 * 社会化用户表
 */
@Entity
@Table(name = "sys_social_user")
interface SocialUser {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long

    /**
     * 第三方系统的唯一ID
     */
    @Key
    val uuid: String

    /**
     * 第三方用户来源
     */
    @Key
    val source: String

    /**
     * 用户的授权令牌
     */
    val accessToken: String

    /**
     * 第三方用户的授权令牌的有效期
     */
    val expireIn: Int?

    /**
     * 刷新令牌
     */
    val refreshToken: String?

    /**
     * 第三方用户的 open id
     */
    val openId: String?

    /**
     * 第三方用户的 ID
     */
    val uid: String?

    /**
     * 个别平台的授权信息
     */
    val accessCode: String?

    /**
     * 第三方用户的 union id
     */
    val unionId: String?

    /**
     * 第三方用户授予的权限
     */
    val scope: String?

    /**
     * 个别平台的授权信息
     */
    val tokenType: String?

    /**
     * id token
     */
    val idToken: String?

    /**
     * 小米平台用户的附带属性
     */
    val macAlgorithm: String?

    /**
     * 小米平台用户的附带属性
     */
    val macKey: String?

    /**
     * 用户的授权code
     */
    val code: String?

    /**
     * Twitter平台用户的附带属性
     */
    val oauthToken: String?

    /**
     * Twitter平台用户的附带属性
     */
    val oauthTokenSecret: String?

    /**
     * 社会化用户与系统用户的关系
     */
    @OneToMany(mappedBy = "socialUser")
    val socialUserAuths: List<SocialUserAuth>
} 