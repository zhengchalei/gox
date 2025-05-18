package com.zhengchalei.gox.modules.system.auth.config

import me.zhyd.oauth.cache.AuthStateCache
import me.zhyd.oauth.config.AuthConfig
import me.zhyd.oauth.request.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


/** JustAuth工厂配置 */
@Configuration
class JustAuthConfig() {

    /** 创建AuthRequest工厂 */
    @Bean
    fun authRequestFactory(
        authStateCache: AuthStateCache,
        justAuthProperties: JustAuthProperties,
    ): Map<String, AuthRequest> {
        val authRequests = mutableMapOf<String, AuthRequest>()

        // 构建各平台的AuthRequest对象
        with(justAuthProperties) {
            // GitHub配置
            if (github.isNotEmpty()) {
                val authConfig =
                    AuthConfig.builder()
                        .clientId(github["clientId"])
                        .clientSecret(github["clientSecret"])
                        .redirectUri(github["redirectUri"])
                        .build()
                authRequests["github"] = AuthGithubRequest(authConfig, authStateCache)
            }

            // Gitee配置
            if (gitee.isNotEmpty()) {
                val authConfig =
                    AuthConfig.builder()
                        .clientId(gitee["clientId"])
                        .clientSecret(gitee["clientSecret"])
                        .redirectUri(gitee["redirectUri"])
                        .build()
                authRequests["gitee"] = AuthGiteeRequest(authConfig, authStateCache)
            }

            // 微信配置
            if (wechat.isNotEmpty()) {
                val authConfig =
                    AuthConfig.builder()
                        .clientId(wechat["clientId"])
                        .clientSecret(wechat["clientSecret"])
                        .redirectUri(wechat["redirectUri"])
                        .build()
                authRequests["wechat"] = AuthWeChatOpenRequest(authConfig, authStateCache)
            }

            // QQ配置
            if (qq.isNotEmpty()) {
                val authConfig =
                    AuthConfig.builder()
                        .clientId(qq["clientId"])
                        .clientSecret(qq["clientSecret"])
                        .redirectUri(qq["redirectUri"])
                        .build()
                authRequests["qq"] = AuthQqRequest(authConfig, authStateCache)
            }

            // 微博配置
            if (weibo.isNotEmpty()) {
                val authConfig =
                    AuthConfig.builder()
                        .clientId(weibo["clientId"])
                        .clientSecret(weibo["clientSecret"])
                        .redirectUri(weibo["redirectUri"])
                        .build()
                authRequests["weibo"] = AuthWeiboRequest(authConfig, authStateCache)
            }

            // 钉钉配置
            if (dingtalk.isNotEmpty()) {
                val authConfig =
                    AuthConfig.builder()
                        .clientId(dingtalk["clientId"])
                        .clientSecret(dingtalk["clientSecret"])
                        .redirectUri(dingtalk["redirectUri"])
                        .build()
                authRequests["dingtalk"] = AuthDingTalkRequest(authConfig, authStateCache)
            }
        }

        return authRequests
    }

    /** 创建AuthStateCache实例，用于缓存授权状态 */
    @Bean
    fun authStateCache(): AuthStateCache {
        // 默认使用内存缓存，生产环境可以替换为Redis等缓存实现
        return object : AuthStateCache {
            private val cache = mutableMapOf<String, String>()

            override fun cache(key: String, value: String) {
                cache[key] = value
            }

            override fun cache(key: String, value: String, timeout: Long) {
                cache[key] = value
                // 这里简单实现，不处理超时逻辑，实际应用中应该使用带过期时间的缓存
            }

            override fun get(key: String): String? {
                return cache[key]
            }

            override fun containsKey(key: String): Boolean {
                return cache.containsKey(key)
            }
        }
    }
}
