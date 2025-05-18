package com.zhengchalei.gox.modules.system.auth.controller

import cn.dev33.satoken.stp.StpUtil
import com.zhengchalei.gox.R
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

/** 无需登录的认证控制器 */
@Tag(name = "认证接口", description = "登录认证相关操作")
@RestController
@RequestMapping("/api")
class AuthController(private val authService: AuthService) {

    /** 用户名密码登录 */
    @Operation(summary = "用户名密码登录")
    @PostMapping("/auth/login")
    fun login(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<LoginResponse> {
        val user: User = authService.login(loginRequest)
        StpUtil.login(user.id, loginRequest.rememberMe)
        val loginResponse = LoginResponse(StpUtil.getTokenValue(), user.username)
        return ResponseEntity.ok(loginResponse)
    }

    /** 获取第三方登录授权地址 */
    @Operation(summary = "获取第三方登录授权地址")
    @GetMapping("/oauth/render/{source}")
    fun renderAuth(@PathVariable source: String): String {
        val authUrl: String = authService.getAuthUrl(source, AuthStateUtils.createState())
        return "redirect:$authUrl"
    }

    /** 第三方登录回调接口 */
    @Operation(summary = "第三方登录回调接口")
    @GetMapping("/oauth/callback/{source}")
    fun callback(
        @PathVariable source: String,
        @ModelAttribute callback: AuthCallback
    ): ResponseEntity<R<Any>> {
        val response = authService.callback(source, callback)
        if (response.ok()) {
            // 授权成功，创建或更新社会化用户并执行登录
            val authUser = response.data
            // 仅仅执行登陆， 如果登陆账号不存在，则保存当前上下文信息， 然后用户绑定或者注册
            val loginResponse = authService.oauth2Login(authUser, callback)
            if (loginResponse.first != null) {
                return ResponseEntity.ok(
                    R(
                        success = true,
                        data = loginResponse.first,
                    )
                )
            }
            return ResponseEntity.ok(
                R(
                    success = false,
                    message = "未绑定用户， 是否注册为新用户?",
                    data = loginResponse.second,
                )
            )
        }

        // 授权失败或未处理的情况，直接返回原始响应
        return ResponseEntity.badRequest().body(R(success = false, message = "授权失败?"))
    }

    /**
     * 根据 oauth-callback 接口返回的 临时凭证 进行注册
     * @param singleUseCredential 第三方登录返回的 临时凭证
     * @return 注册结果
     */
    @Operation(summary = "根据 oauth-callback 接口返回的 临时凭证 进行注册")
    @GetMapping("/register/oauth/{singleUseCredential}")
    fun registerBySingleUseCredential(@PathVariable singleUseCredential: String): ResponseEntity<R<LoginResponse>> {
        // 根据 临时凭证 进行注册
        val loginResponse = authService.registerBySingleUseCredential(singleUseCredential)
        return ResponseEntity.ok(R(success = true, message = "注册成功", data = loginResponse))
    }
}
