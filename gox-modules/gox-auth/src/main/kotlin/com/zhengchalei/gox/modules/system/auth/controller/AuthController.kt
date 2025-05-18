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
import me.zhyd.oauth.utils.AuthStateUtils
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 无需登录的认证控制器
 */
@Tag(name = "认证接口", description = "登录认证相关操作")
@RestController
@RequestMapping("/api")
class AuthController(private val authService: AuthService) {

    /**
     * 用户名密码登录
     */
    @Operation(summary = "用户名密码登录")
    @PostMapping("/auth/login")
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
     */
    @Operation(summary = "获取第三方登录授权地址")
    @GetMapping("/oauth/render/{source}")
    fun renderAuth(@PathVariable source: String): ResponseEntity<String> {
        val authUrl: String = authService.getAuthUrl(source, AuthStateUtils.createState())
        return ResponseEntity.ok(authUrl)
    }

    /**
     * 第三方登录回调接口
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
} 