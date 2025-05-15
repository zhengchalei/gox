package com.zhengchalei.gox.modules.system.auth.service

import com.zhengchalei.gox.modules.system.auth.dto.LoginRequest
import com.zhengchalei.gox.modules.system.entity.User

interface AuthService {
    fun login(request: LoginRequest): User
} 