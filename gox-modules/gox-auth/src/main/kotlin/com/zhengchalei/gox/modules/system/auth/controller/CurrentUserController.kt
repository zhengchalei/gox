package com.zhengchalei.gox.modules.system.auth.controller

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import com.zhengchalei.gox.R
import com.zhengchalei.gox.modules.system.auth.dto.SocialUserAuthDetailDTO
import com.zhengchalei.gox.modules.system.auth.service.AuthService
import com.zhengchalei.gox.modules.system.entity.dto.UserDetailDTO
import com.zhengchalei.gox.modules.system.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.*

/** 需要登录的认证控制器 所有接口都需要登录才能访问 */
@Tag(name = "用户认证", description = "需要登录的认证相关操作")
@RestController
@RequestMapping("/api")
@SaCheckLogin
class CurrentUserController(private val authService: AuthService, private val userService: UserService) {

    /** 登出 */
    @Operation(summary = "登出")
    @PostMapping(value = ["/logout"])
    fun logout(): R<Boolean> {
        StpUtil.logout()
        return R(success = true)
    }

    /** 获取当前登录用户信息 */
    @Operation(summary = "获取当前登录用户信息")
    @GetMapping(value = ["/user/info"])
    fun getCurrentUserInfo(): R<UserDetailDTO> {
        val loginId = StpUtil.getLoginIdAsLong()
        val userInfo = userService.findById(loginId)
        return R(success = true, data = userInfo)
    }

    /** 获取当前登录用户绑定的所有OAuth2账号 */
    @Operation(summary = "获取当前登录用户绑定的所有OAuth2账号")
    @GetMapping(value = ["/oauth/my-bindings"])
    fun listMyBindings(): R<List<SocialUserAuthDetailDTO>> {
        val loginId = StpUtil.getLoginIdAsLong()
        val socialUsers: List<SocialUserAuthDetailDTO> = authService.findBindSocialUsers(loginId)
        return R(success = true, data = socialUsers)
    }

    /** 发起绑定其他第三方平台 */
    @Operation(summary = "发起绑定其他第三方平台")
    @GetMapping(value = ["/oauth/bind-url/{source}"])
    fun getBindUrl(@PathVariable source: String, response: HttpServletResponse) {
        val url = authService.bindCurrentUser(source)
        response.sendRedirect(url)
    }

    /** 当前登录用户解绑指定第三方平台 */
    @Operation(summary = "当前登录用户解绑指定第三方平台")
    @DeleteMapping(value = ["/oauth/unbind/{source}"])
    fun unbindMyAccount(@PathVariable source: String): R<Boolean> {
        val loginId = StpUtil.getLoginIdAsLong()
        val result = authService.unbindUser(loginId, source)
        return R(success = true, data = result)
    }

    /** 检查当前登录用户是否已绑定指定平台 */
    @Operation(summary = "检查当前登录用户是否已绑定指定平台")
    @GetMapping(value = ["/oauth/is-bound/{source}"])
    fun isBound(@PathVariable source: String): R<Boolean> {
        val loginId = StpUtil.getLoginIdAsLong()
        val result = authService.isBound(loginId, source)
        return R(success = true, data = result)
    }
}
