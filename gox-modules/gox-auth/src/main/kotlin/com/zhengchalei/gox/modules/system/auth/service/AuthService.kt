package com.zhengchalei.gox.modules.system.auth.service

import com.zhengchalei.gox.modules.system.auth.dto.LoginRequest
import com.zhengchalei.gox.modules.system.auth.dto.SocialUserAuthDetailDTO
import com.zhengchalei.gox.modules.system.auth.dto.SocialUserDetailDTO
import com.zhengchalei.gox.modules.system.entity.User
import me.zhyd.oauth.model.AuthCallback
import me.zhyd.oauth.model.AuthResponse
import me.zhyd.oauth.model.AuthUser

interface AuthService {
    fun login(request: LoginRequest): User

    /**
     * 获取第三方登录地址
     *
     * @param source 平台来源
     * @param state 状态值
     * @return 登录授权地址
     */
    fun getAuthUrl(source: String, state: String): String

    /**
     * 授权回调
     *
     * @param source 平台来源
     * @param callback 回调参数
     * @return 授权响应
     */
    fun callback(source: String, callback: AuthCallback): AuthResponse<AuthUser>

    /**
     * 根据UUID和来源查询社会化用户
     *
     * @param uuid 第三方平台用户UUID
     * @param source 平台来源
     * @return 社会化用户详情
     */
    fun findByUuidAndSource(uuid: String, source: String): SocialUserDetailDTO?

    /**
     * 创建或更新社会化用户
     *
     * @param authUser 认证用户信息
     * @return 社会化用户详情
     */
    fun createOrUpdateSocialUser(authUser: AuthUser): SocialUserDetailDTO

    /**
     * 绑定社会化用户到系统用户
     *
     * @param userId 系统用户ID
     * @param socialUserId 社会化用户ID
     * @return 社会化用户关系详情
     */
    fun bindUser(userId: Long, socialUserId: Long): SocialUserAuthDetailDTO

    /**
     * 解绑社会化用户
     *
     * @param userId 系统用户ID
     * @param source 平台来源
     * @return 是否解绑成功
     */
    fun unbindUser(userId: Long, source: String): Boolean

    /**
     * 查询用户绑定的所有社会化账号
     *
     * @param userId 系统用户ID
     * @return 社会化用户关系详情列表
     */
    fun findBindSocialUsers(userId: Long): List<SocialUserAuthDetailDTO>

    /**
     * 检查用户是否已绑定指定平台的社会化账号
     *
     * @param userId 系统用户ID
     * @param source 平台来源
     * @return 是否已绑定
     */
    fun isBound(userId: Long, source: String): Boolean
} 