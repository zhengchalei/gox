package com.zhengchalei.gox.modules.system.auth.dto

import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank(message = "用户名不能为空")
    val username: String,
    
    @field:NotBlank(message = "密码不能为空")
    val password: String
) 