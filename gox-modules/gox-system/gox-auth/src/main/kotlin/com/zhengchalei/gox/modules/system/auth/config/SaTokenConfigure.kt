package com.zhengchalei.gox.modules.system.auth.config

@Configuration
class SaTokenConfigure : WebMvcConfigurer {
    // 注册拦截器
    public override fun addInterceptors(registry: InterceptorRegistry) {
        // 注册 Sa-Token 拦截器，校验规则为 StpUtil.checkLogin() 登录校验。
        registry.addInterceptor(SaInterceptor({ handle -> StpUtil.checkLogin() }))
            .addPathPatterns("/**")
            .excludePathPatterns("/user/doLogin")
    }
}
