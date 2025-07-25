package com.zhengchalei.gox.modules.system.auth.config

import cn.dev33.satoken.interceptor.SaInterceptor
import cn.dev33.satoken.stp.StpUtil
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class SaTokenConfigure : WebMvcConfigurer {
    // 注册拦截器
    override fun addInterceptors(registry: InterceptorRegistry) {
        // 注册 Sa-Token 拦截器，校验规则为 StpUtil.checkLogin() 登录校验。
        registry.addInterceptor(SaInterceptor { handle ->
            try {
                StpUtil.checkLogin()
            } catch (e: Exception) {
            }
        })
            .addPathPatterns("/api/**")
            .excludePathPatterns(
                "/api/auth/login",
                "/api/oauth/callback/*",
                "/api/oauth/render/*",
                "/api/register/oauth/*"
            )
    }
}
