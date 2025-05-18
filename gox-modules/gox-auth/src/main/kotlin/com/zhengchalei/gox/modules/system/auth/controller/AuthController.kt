package com.zhengchalei.gox.modules.system.auth.controller

import cn.dev33.satoken.stp.StpUtil
import com.zhengchalei.gox.modules.system.auth.dto.LoginRequest
import com.zhengchalei.gox.modules.system.auth.dto.LoginResponse
import com.zhengchalei.gox.modules.system.auth.service.AuthService
import com.zhengchalei.gox.modules.system.entity.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import me.zhyd.oauth.model.AuthCallback
import me.zhyd.oauth.model.AuthResponse
import me.zhyd.oauth.model.AuthUser
import me.zhyd.oauth.utils.AuthStateUtils
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@Tag(name = "登陆认证", description = "登陆认证相关操作")
@RestController
@RequestMapping("/api/public/auth")
class AuthController(private val authService: AuthService) {

    @Operation(summary = "登录")
    @PostMapping("/login")
    fun login(
        @Valid
        @RequestBody loginRequest: LoginRequest
    ): ResponseEntity<LoginResponse> {
        val user: User = authService.login(loginRequest)
        StpUtil.login(user.id, loginRequest.rememberMe)
        val loginResponse = LoginResponse(StpUtil.getTokenValue(), user.username)
        return ResponseEntity.ok(loginResponse)
    }

    /**
     * 获取第三方登录授权地址
     *
     * @param source 平台来源
     * @return 授权地址
     */
    @GetMapping("/oauth/render/{source}")
    fun renderAuth(@PathVariable source: String): String {
        return authService.getAuthUrl(source, AuthStateUtils.createState())
    }

    /**
     * 第三方登录回调接口
     *
     * @param source 平台来源
     * @param callback 回调参数
     * @return 授权用户信息
     */
    @GetMapping("/oauth/callback/{source}")
    fun callback(
        @PathVariable source: String,
        @ModelAttribute callback: AuthCallback
    ): AuthResponse<AuthUser> {
        val response = authService.callback(source, callback)
        if (response.code == 2000) {
            // 授权成功，创建或更新社会化用户
            val authUser = response.data
            authService.createOrUpdateSocialUser(authUser)
        }
        return response
    }

    /**
     * 获取用户绑定的所有社会化账号
     *
     * @param userId 用户ID
     * @return 社会化用户关系列表
     */
    @GetMapping("/oauth/bindings/{userId}")
    fun listBindings(@PathVariable userId: Long): List<SocialUserAuthDetailDTO> {
        return authService.findBindSocialUsers(userId)
    }

    /**
     * 绑定社会化用户到系统用户
     *
     * @param userId 系统用户ID
     * @param socialUserId 社会化用户ID
     * @return 绑定关系详情
     */
    @PostMapping("/oauth/bind")
    fun bindSocialUser(
        @RequestParam userId: Long,
        @RequestParam socialUserId: Long
    ): SocialUserAuthDetailDTO {
        return authService.bindUser(userId, socialUserId)
    }

    /**
     * 解绑社会化用户
     *
     * @param userId 系统用户ID
     * @param source 平台来源
     * @return 是否解绑成功
     */
    @DeleteMapping("/oauth/unbind")
    fun unbindSocialUser(
        @RequestParam userId: Long,
        @RequestParam source: String
    ): Boolean {
        return authService.unbindUser(userId, source)
    }

    /**
     * 检查用户是否已绑定指定平台的社会化账号
     *
     * @param userId 系统用户ID
     * @param source 平台来源
     * @return 是否已绑定
     */
    @GetMapping("/oauth/is-bound")
    fun isBound(
        @RequestParam userId: Long,
        @RequestParam source: String
    ): Boolean {
        return authService.isBound(userId, source)
    }
} 