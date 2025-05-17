package com.zhengchalei.gox.modules.system.config

import com.zhengchalei.gox.modules.system.interceptor.RoutePermissionInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val routePermissionInterceptor: RoutePermissionInterceptor
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(routePermissionInterceptor)
            .addPathPatterns("/api/**") // 拦截所有API请求
            .excludePathPatterns(
                "/api/v1/auth/**",      // 排除认证相关接口
                "/api/v1/sys/route-permission/**", // 排除路由权限管理接口
                "/swagger-ui/**",        // 排除Swagger UI
                "/v3/api-docs/**"        // 排除API文档
            )
    }
} 