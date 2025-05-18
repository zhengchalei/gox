package com.zhengchalei.gox.modules.system.auth.controller

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import com.zhengchalei.gox.modules.system.auth.dto.SocialUserAuthDetailDTO
import com.zhengchalei.gox.modules.system.auth.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

/**
 * 需要登录的认证控制器
 * 所有接口都需要登录才能访问
 */
@Tag(name = "用户认证", description = "需要登录的认证相关操作")
@RestController
@RequestMapping("/api")
@SaCheckLogin
class OAuth2Controller(private val authService: AuthService) {

    /**
     * 获取当前登录用户绑定的所有OAuth2账号
     */
    @Operation(summary = "获取当前登录用户绑定的所有OAuth2账号")
    @GetMapping("/oauth/my-bindings")
    fun listMyBindings(): List<SocialUserAuthDetailDTO> {
        val loginId = StpUtil.getLoginIdAsLong()
        return authService.findBindSocialUsers(loginId)
    }

    /**
     * 获取当前登录用户绑定第三方平台的授权地址
     */
    @Operation(summary = "获取当前登录用户绑定第三方平台的授权地址")
    @GetMapping("/oauth/bind-url/{source}")
    fun getBindUrl(@PathVariable source: String): String {
        return authService.bindCurrentUser(source)
    }

    /**
     * 当前登录用户解绑OAuth2账号
     */
    @Operation(summary = "当前登录用户解绑OAuth2账号")
    @DeleteMapping("/oauth/unbind/{source}")
    fun unbindMyAccount(@PathVariable source: String): Boolean {
        val loginId = StpUtil.getLoginIdAsLong()
        return authService.unbindUser(loginId, source)
    }

    /**
     * 检查当前登录用户是否已绑定指定平台
     */
    @Operation(summary = "检查当前登录用户是否已绑定指定平台")
    @GetMapping("/oauth/is-bound/{source}")
    fun isBound(@PathVariable source: String): Boolean {
        val loginId = StpUtil.getLoginIdAsLong()
        return authService.isBound(loginId, source)
    }

    /**
     * 登出
     */
    @Operation(summary = "登出")
    @PostMapping("/logout")
    fun logout(): Boolean {
        StpUtil.logout()
        return true
    }
} 