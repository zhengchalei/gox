package com.zhengchalei.gox.modules.system.auth.config

import cn.dev33.satoken.interceptor.SaInterceptor
import cn.dev33.satoken.stp.StpUtil
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class SaTokenConfigure : WebMvcConfigurer {
    // 注册拦截器
    public override fun addInterceptors(registry: InterceptorRegistry) {
        // 注册 Sa-Token 拦截器，校验规则为 StpUtil.checkLogin() 登录校验。
        registry.addInterceptor(SaInterceptor { handle -> StpUtil.checkLogin() })
            .addPathPatterns("/**")
            // 排除路径, swagger 文档
            .excludePathPatterns("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
            .excludePathPatterns("/api/public/**")
    }
}
