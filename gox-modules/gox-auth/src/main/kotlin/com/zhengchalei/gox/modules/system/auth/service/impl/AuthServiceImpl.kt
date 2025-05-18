package com.zhengchalei.gox.modules.system.auth.service.impl

import com.zhengchalei.gox.modules.system.auth.dto.*
import com.zhengchalei.gox.modules.system.auth.entity.SocialUser
import com.zhengchalei.gox.modules.system.auth.entity.SocialUserAuth
import com.zhengchalei.gox.modules.system.auth.entity.userId
import com.zhengchalei.gox.modules.system.auth.repository.SocialUserAuthRepository
import com.zhengchalei.gox.modules.system.auth.repository.SocialUserRepository
import com.zhengchalei.gox.modules.system.auth.service.AuthService
import com.zhengchalei.gox.modules.system.entity.User
import com.zhengchalei.gox.modules.system.entity.username
import com.zhengchalei.gox.util.PasswordUtil
import me.zhyd.oauth.config.AuthConfig
import me.zhyd.oauth.exception.AuthException
import me.zhyd.oauth.model.AuthCallback
import me.zhyd.oauth.model.AuthResponse
import me.zhyd.oauth.model.AuthUser
import me.zhyd.oauth.request.AuthRequest
import me.zhyd.oauth.utils.AuthStateUtils
import org.babyfish.jimmer.kt.new
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * 认证服务实现类
 */
@Service
class AuthServiceImpl(
    private val sqlClient: KSqlClient,
    private val socialUserRepository: SocialUserRepository,
    private val socialUserAuthRepository: SocialUserAuthRepository,
    private val authRequestFactory: Map<String, AuthRequest>
) : AuthService {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun login(request: LoginRequest): User {
        val user: User = this.sqlClient.createQuery(User::class) {
            where(table.username eq request.username)
            select(table)
        }.fetchOneOrNull() ?: throw IllegalArgumentException("用户不存在")

        if (!PasswordUtil.matches(request.password, user.password)) {
            log.warn("密码错误: {}", user.username)
            throw IllegalArgumentException("密码错误")
        }

        if (!user.enabled) {
            log.warn("用户被禁用: {}", user.username)
            throw IllegalArgumentException("用户被禁用")
        }

        return user
    }

    /**
     * 获取第三方登录地址
     */
    override fun getAuthUrl(source: String, state: String): String {
        val authRequest = getAuthRequest(source)
        val finalState = state.ifEmpty { AuthStateUtils.createState() }
        return authRequest.authorize(finalState)
    }

    /**
     * 授权回调
     */
    override fun callback(source: String, callback: AuthCallback): AuthResponse<AuthUser> {
        val authRequest = getAuthRequest(source)
        return authRequest.login(callback)
    }

    /**
     * 根据UUID和来源查询社会化用户
     */
    override fun findByUuidAndSource(uuid: String, source: String): SocialUserDetailDTO? {
        return socialUserRepository.findByUuidAndSource(uuid, source)
    }

    /**
     * 创建或更新社会化用户
     */
    @Transactional
    override fun createOrUpdateSocialUser(authUser: AuthUser): SocialUserDetailDTO {
        val existingSocialUser = findByUuidAndSource(authUser.uuid, authUser.source)

        // 如果已存在，则返回现有用户
        if (existingSocialUser != null) {
            return existingSocialUser
        }

        // 创建新的社会化用户
        val socialUserDraft = new(SocialUser::class).by {
            uuid = authUser.uuid
            source = authUser.source
            accessToken = authUser.token.accessToken
            expireIn = authUser.token.expireIn
            refreshToken = authUser.token.refreshToken
            openId = authUser.token.openId
            uid = authUser.token.uid
            accessCode = authUser.token.accessCode
            unionId = authUser.token.unionId
            scope = authUser.token.scope
            tokenType = authUser.token.tokenType
            idToken = authUser.token.idToken
            macAlgorithm = authUser.token.macAlgorithm
            macKey = authUser.token.macKey
            code = authUser.token.code
            oauthToken = authUser.token.oauthToken
            oauthTokenSecret = authUser.token.oauthTokenSecret
            createdTime = LocalDateTime.now()
            updatedTime = LocalDateTime.now()
        }

        val savedSocialUser = socialUserRepository.save(socialUserDraft)
        return socialUserRepository.findById(savedSocialUser.id)
            ?: throw IllegalStateException("无法获取刚刚创建的社会化用户")
    }

    /**
     * 绑定社会化用户到系统用户
     */
    @Transactional
    override fun bindUser(userId: Long, socialUserId: Long): SocialUserAuthDetailDTO {
        // 检查是否已绑定
        val existingAuth = socialUserAuthRepository.findBySocialUserId(socialUserId)
        if (existingAuth != null) {
            throw IllegalStateException("该社会化账号已绑定到其他用户")
        }

        // 创建绑定关系
        val socialUserAuthDraft = new(SocialUserAuth::class).by {
            // 这里我们假设 user 和 socialUser 字段都是对象引用
            // 在实际使用中，需要先查询这些对象，然后设置引用
            this.user = User::class.new { id = userId }
            this.socialUser = SocialUser::class.new { id = socialUserId }
            createdTime = LocalDateTime.now()
            updatedTime = LocalDateTime.now()
        }

        val savedAuth = socialUserAuthRepository.save(socialUserAuthDraft)
        return socialUserAuthRepository.findBySocialUserId(savedAuth.socialUser.id)
            ?: throw IllegalStateException("无法获取刚刚创建的社会化用户关系")
    }

    /**
     * 解绑社会化用户
     */
    @Transactional
    override fun unbindUser(userId: Long, source: String): Boolean {
        val auth = socialUserAuthRepository.findByUserIdAndSource(userId, source)
            ?: return false

        return socialUserAuthRepository.deleteByUserIdAndSocialUserId(userId, auth.socialUserId)
    }

    /**
     * 查询用户绑定的所有社会化账号
     */
    override fun findBindSocialUsers(userId: Long): List<SocialUserAuthDetailDTO> {
        return socialUserAuthRepository.findByUserId(userId)
    }

    /**
     * 检查用户是否已绑定指定平台的社会化账号
     */
    override fun isBound(userId: Long, source: String): Boolean {
        return socialUserAuthRepository.findByUserIdAndSource(userId, source) != null
    }

    /**
     * 获取AuthRequest
     */
    private fun getAuthRequest(source: String): AuthRequest {
        return authRequestFactory[source] ?: throw AuthException("未知的授权来源: $source")
    }
    
    /**
     * 社会化用户创建DTO实现类
     */
    private data class SocialUserCreateDTOImpl(
        override val uuid: String,
        override val source: String,
        override val accessToken: String,
        override val expireIn: Int?,
        override val refreshToken: String?,
        override val openId: String?,
        override val uid: String?,
        override val accessCode: String?,
        override val unionId: String?,
        override val scope: String?,
        override val tokenType: String?,
        override val idToken: String?,
        override val macAlgorithm: String?,
        override val macKey: String?,
        override val code: String?,
        override val oauthToken: String?,
        override val oauthTokenSecret: String?
    ) : SocialUserCreateDTO
    
    /**
     * 社会化用户与系统用户关系表创建DTO实现类
     */
    private data class SocialUserAuthCreateDTOImpl(
        override val userId: Long,
        override val socialUserId: Long
    ) : SocialUserAuthCreateDTO
} 