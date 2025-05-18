package com.zhengchalei.gox.modules.system.auth.controller

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import com.zhengchalei.gox.modules.system.auth.dto.LoginRequest
import com.zhengchalei.gox.modules.system.auth.dto.LoginResponse
import com.zhengchalei.gox.modules.system.auth.dto.SocialUserAuthDetailDTO
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
    @Operation(summary = "获取第三方登录授权地址")
    @GetMapping("/oauth/render/{source}")
    fun renderAuth(@PathVariable source: String): String {
        return authService.getAuthUrl(source, AuthStateUtils.createState())
    }

    /**
     * 第三方登录回调接口
     *
     * @param source 平台来源
     * @param callback 回调参数
     * @return 登录响应或授权用户信息
     */
    @Operation(summary = "第三方登录回调接口")
    @GetMapping("/oauth/callback/{source}")
    fun callback(
        @PathVariable source: String,
        @ModelAttribute callback: AuthCallback
    ): ResponseEntity<Any> {
        val response = authService.callback(source, callback)
        if (response.code == 2000) {
            // 授权成功，创建或更新社会化用户并执行登录
            val authUser = response.data
            
            // 检查状态值是否包含绑定信息
            val state = callback.state ?: ""
            if (state.startsWith("bind_")) {
                // 这是一个绑定操作，提取用户ID并执行绑定
                val parts = state.split("_")
                if (parts.size >= 2) {
                    try {
                        val userId = parts[1].toLong()
                        // 创建或更新社会化用户
                        val socialUser = authService.createOrUpdateSocialUser(authUser)
                        // 执行绑定
                        val bindResult = authService.bindUser(userId, socialUser.id)
                        return ResponseEntity.ok(bindResult)
                    } catch (e: Exception) {
                        return ResponseEntity.badRequest().body("绑定失败: " + e.message)
                    }
                }
            } else {
                // 执行登录或注册
                val loginResponse = authService.loginOrRegisterBySocial(authUser)
                return ResponseEntity.ok(loginResponse)
            }
        }
        
        // 授权失败或未处理的情况，直接返回原始响应
        return ResponseEntity.ok(response)
    }

    /**
     * 获取当前登录用户绑定第三方平台的授权地址
     *
     * @param source 平台来源
     * @return 授权地址
     */
    @Operation(summary = "获取当前登录用户绑定第三方平台的授权地址")
    @SaCheckLogin
    @GetMapping("/oauth/bind-url/{source}")
    fun getBindUrl(@PathVariable source: String): String {
        return authService.bindCurrentUser(source)
    }

    /**
     * 获取用户绑定的所有社会化账号
     *
     * @param userId 用户ID
     * @return 社会化用户关系列表
     */
    @Operation(summary = "获取用户绑定的所有社会化账号")
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
    @Operation(summary = "绑定社会化用户到系统用户")
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
    @Operation(summary = "解绑社会化用户")
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
    @Operation(summary = "检查用户是否已绑定指定平台的社会化账号")
    @GetMapping("/oauth/is-bound")
    fun isBound(
        @RequestParam userId: Long,
        @RequestParam source: String
    ): Boolean {
        return authService.isBound(userId, source)
    }
} 