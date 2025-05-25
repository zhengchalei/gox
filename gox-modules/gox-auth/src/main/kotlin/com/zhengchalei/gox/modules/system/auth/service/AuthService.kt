package com.zhengchalei.gox.modules.system.auth.service

import cn.dev33.satoken.stp.StpUtil
import com.zhengchalei.gox.modules.system.auth.dto.LoginRequest
import com.zhengchalei.gox.modules.system.auth.dto.LoginResponse
import com.zhengchalei.gox.modules.system.auth.dto.SocialUserAuthDetailDTO
import com.zhengchalei.gox.modules.system.auth.entity.SocialUser
import com.zhengchalei.gox.modules.system.auth.entity.SocialUserAuth
import com.zhengchalei.gox.modules.system.auth.entity.by
import com.zhengchalei.gox.modules.system.auth.repository.SocialUserAuthRepository
import com.zhengchalei.gox.modules.system.auth.repository.SocialUserRepository
import com.zhengchalei.gox.modules.system.entity.User
import com.zhengchalei.gox.modules.system.entity.by
import com.zhengchalei.gox.modules.system.entity.dto.UserDetailDTO
import com.zhengchalei.gox.modules.system.entity.id
import com.zhengchalei.gox.modules.system.entity.username
import com.zhengchalei.gox.modules.system.repository.UserRepository
import com.zhengchalei.gox.util.PasswordUtil
import me.zhyd.oauth.exception.AuthException
import me.zhyd.oauth.model.AuthCallback
import me.zhyd.oauth.model.AuthResponse
import me.zhyd.oauth.model.AuthUser
import me.zhyd.oauth.request.AuthRequest
import me.zhyd.oauth.utils.AuthStateUtils
import org.babyfish.jimmer.kt.makeIdOnly
import org.babyfish.jimmer.kt.new
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

/**
 * 认证服务实现类
 */
@Service
@Transactional(rollbackFor = [Exception::class])
class AuthService(
    private val sqlClient: KSqlClient,
    private val socialUserRepository: SocialUserRepository,
    private val socialUserAuthRepository: SocialUserAuthRepository,
    private val userRepository: UserRepository,
    private val authRequestFactory: Map<String, AuthRequest>
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    // 来自于 oauth2 login 登陆用户， 但是未注册的， 存储登陆信息
    private val noRegisterOauthLoginAuthUser = HashMap<String, AuthUser>()


    fun login(request: LoginRequest): User {
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
    fun getAuthUrl(source: String, state: String): String {
        val authRequest = getAuthRequest(source)
        val finalState = state.ifEmpty { AuthStateUtils.createState() }
        return authRequest.authorize(finalState)
    }

    /**
     * 授权回调
     */
    fun callback(source: String, callback: AuthCallback): AuthResponse<AuthUser> {
        val authRequest = getAuthRequest(source)
        return authRequest.login(callback)
    }

    /**
     * 绑定社会化用户到系统用户
     */
    fun bindUser(userId: Long, socialUserId: Long): SocialUserAuthDetailDTO {
        // 检查是否已绑定
        val existingAuth = socialUserAuthRepository.findBySocialUserId(socialUserId)
        if (existingAuth != null) {
            throw IllegalStateException("该社会化账号已绑定到其他用户")
        }

        // 创建绑定关系
        val socialUserAuthDraft = new(SocialUserAuth::class).by {
            this.user = makeIdOnly(userId)
            this.socialUser = makeIdOnly(socialUserId)
        }

        val savedAuth = socialUserAuthRepository.save(socialUserAuthDraft)
        return socialUserAuthRepository.findBySocialUserId(savedAuth.socialUser.id)
            ?: throw IllegalStateException("无法获取刚刚创建的社会化用户关系")
    }

    /**
     * 解绑社会化用户
     */
    fun unbindUser(userId: Long, source: String): Boolean {
        val auth = socialUserAuthRepository.findByUserIdAndSource(userId, source)
            ?: return false

        return socialUserAuthRepository.deleteByUserIdAndSocialUserId(userId, auth.socialUserId)
    }

    /**
     * 查询用户绑定的所有社会化账号
     */
    fun findBindSocialUsers(userId: Long): List<SocialUserAuthDetailDTO> {
        return socialUserAuthRepository.findByUserId(userId)
    }

    /**
     * 检查用户是否已绑定指定平台的社会化账号
     */
    fun isBound(userId: Long, source: String): Boolean {
        return socialUserAuthRepository.findByUserIdAndSource(userId, source) != null
    }

    /**
     * 当前登录用户绑定第三方平台
     */
    fun bindCurrentUser(source: String): String {
        // 获取当前登录用户ID
        val loginId = StpUtil.getLoginIdAsLong()
        // 生成状态值，包含用户ID信息，用于回调时识别用户
        val state = "bind_${loginId}_${System.currentTimeMillis()}"
        // 返回授权地址
        return getAuthUrl(source, state)
    }

    /**
     * 第三方登录
     */
    fun oauth2Login(authUser: AuthUser, callback: AuthCallback): Pair<LoginResponse?, String?> {
        // 判断是一个绑定的请求
        val state = callback.state
        if (state != null && state.startsWith("bind_")) {
            val userId: Long = StpUtil.getLoginIdAsLong()
            bindUser(userId, userId)
            return Pair(LoginResponse(StpUtil.getTokenValue(), authUser.username), null)
        }

        // 如果当前用户未登陆
        val id = this.sqlClient.createQuery(User::class) {
            where(table.username eq authUser.username)
            select(table.id)
        }.fetchOneOrNull()
        if (id != null) {
            StpUtil.login(id)
            return Pair(LoginResponse(StpUtil.getTokenValue(), authUser.username), null)
        }
        val singleUseCredential: String = UUID.randomUUID().toString()
        noRegisterOauthLoginAuthUser[singleUseCredential] = authUser
        return Pair(null, singleUseCredential)
    }

    /**
     * 创建 OAUTH Login User
     */
    fun createOauthLoginUser(authUser: AuthUser): UserDetailDTO {
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

        // 创建用户
        // 创建新用户，注意User是接口，需要使用new方法创建
        val user: User = this.userRepository.save(new(User::class).by {
            this.username = authUser.username
            this.nickname = authUser.nickname
            this.password = PasswordUtil.encode(UUID.randomUUID().toString())
            this.enabled = true
            this.createdTime = LocalDateTime.now()
            this.updatedTime = LocalDateTime.now()
            // 在User接口中没有nickname, avatar和email字段，需要确认实际字段名或添加这些字段
        })

        // 创建绑定关系
        bindUser(user.id, savedSocialUser.id)

        return this.userRepository.sql.findById(UserDetailDTO::class, user.id)
            ?: throw IllegalStateException("无法获取刚刚创建的用户")
    }

    /**
     * 根据 临时凭证 进行注册
     */
    fun registerBySingleUseCredential(singleUseCredential: String): LoginResponse {
        val authUser = noRegisterOauthLoginAuthUser[singleUseCredential]
        if (authUser == null) {
            throw IllegalArgumentException("未找到未注册的第三方登录用户")
        }
        val user = createOauthLoginUser(authUser)
        StpUtil.login(user.id)
        return LoginResponse(StpUtil.getTokenValue(), user.username)
    }

    /**
     * 获取AuthRequest
     */
    private fun getAuthRequest(source: String): AuthRequest {
        return authRequestFactory[source] ?: throw AuthException("未知的授权来源: $source")
    }
}