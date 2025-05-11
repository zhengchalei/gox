package com.zhengchalei.gox.modules.system.auth.service

import com.zhengchalei.gox.modules.system.auth.dto.LoginRequest
import com.zhengchalei.gox.modules.system.auth.dto.LoginResponse

interface AuthService {
    fun login(request: LoginRequest): LoginResponse
} 