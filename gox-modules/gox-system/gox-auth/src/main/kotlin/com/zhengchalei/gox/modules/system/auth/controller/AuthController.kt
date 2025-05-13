package com.zhengchalei.gox.modules.system.auth.controller

import com.zhengchalei.gox.modules.system.auth.dto.LoginRequest
import com.zhengchalei.gox.modules.system.auth.dto.LoginResponse
import com.zhengchalei.gox.modules.system.auth.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@Tag(name = "登陆认证", description = "登陆认证相关操作")
@RestController
@RequestMapping("/api/public/auth")
class AuthController(private val authService: AuthService) {

    @Operation(summary = "登录")
    @PostMapping("/login")
    fun login(
        @Valid
        @RequestBody loginRequest: LoginRequest
    ): LoginResponse {
        val login: LoginResponse = authService.login(loginRequest)
        return login
    }

} 