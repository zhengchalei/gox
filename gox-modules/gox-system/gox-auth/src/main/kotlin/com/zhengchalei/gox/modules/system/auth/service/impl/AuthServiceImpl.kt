package com.zhengchalei.gox.modules.system.auth.service.impl

import com.zhengchalei.gox.modules.system.auth.dto.LoginRequest
import com.zhengchalei.gox.modules.system.auth.dto.LoginResponse
import com.zhengchalei.gox.modules.system.auth.service.AuthService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl : AuthService {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun login(request: LoginRequest): LoginResponse {
        // 实际项目中需要验证用户名和密码
        // 仅用于演示
        return LoginResponse(
            token = "demo.token.${request.username}",
            username = request.username
        )
    }
} 