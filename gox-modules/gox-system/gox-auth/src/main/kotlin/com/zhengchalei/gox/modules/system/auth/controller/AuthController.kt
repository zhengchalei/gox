package com.zhengchalei.gox.modules.system.auth.controller

import com.zhengchalei.gox.modules.system.auth.dto.LoginRequest
import com.zhengchalei.gox.modules.system.auth.dto.LoginResponse
import com.zhengchalei.gox.modules.system.auth.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/public/auth")
class AuthController(private val authService: AuthService) {

    @Operation(summary = "登录")
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): LoginResponse {
        return authService.login(request)
    }

} 