package com.zhengchalei.gox.modules.system.auth.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "登录请求")
data class LoginRequest(

    @Schema(description = "用户名", required = true, example = "admin")
    @NotBlank(message = "用户名不能为空")
    val username: String,

    @Schema(description = "密码", required = true, example = "123456")
    @NotBlank(message = "密码不能为空")
    val password: String,

    // rememberMe: Boolean = false
    @Schema(description = "记住我", required = false, example = "false")
    val rememberMe: Boolean = false
)