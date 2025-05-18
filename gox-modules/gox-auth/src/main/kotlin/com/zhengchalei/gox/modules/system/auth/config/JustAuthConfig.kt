package com.zhengchalei.gox.modules.system.auth.config

import me.zhyd.oauth.cache.AuthStateCache
import me.zhyd.oauth.config.AuthConfig
import me.zhyd.oauth.config.AuthDefaultSource
import me.zhyd.oauth.request.*
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * JustAuth 第三方登录配置类
 */
@Configuration
@EnableConfigurationProperties(JustAuthProperties::class)
class JustAuthConfig(private val justAuthProperties: JustAuthProperties) {

    /**
     * 创建AuthRequest工厂
     */
    @Bean
    fun authRequestFactory(authStateCache: AuthStateCache): Map<String, AuthRequest> {
        val authRequests = mutableMapOf<String, AuthRequest>()
        
        // 构建各平台的AuthRequest对象
        with(justAuthProperties) {
            // GitHub配置
            github?.let {
                val config = AuthConfig.builder()
                    .clientId(it.clientId)
                    .clientSecret(it.clientSecret)
                    .redirectUri(it.redirectUri)
                    .build()
                authRequests[AuthDefaultSource.GITHUB.name] = AuthGithubRequest(config, authStateCache)
            }
            
            // Gitee配置
            gitee?.let {
                val config = AuthConfig.builder()
                    .clientId(it.clientId)
                    .clientSecret(it.clientSecret)
                    .redirectUri(it.redirectUri)
                    .build()
                authRequests[AuthDefaultSource.GITEE.name] = AuthGiteeRequest(config, authStateCache)
            }
            
            // 微信配置
            wechat?.let {
                val config = AuthConfig.builder()
                    .clientId(it.clientId)
                    .clientSecret(it.clientSecret)
                    .redirectUri(it.redirectUri)
                    .build()
                authRequests[AuthDefaultSource.WECHAT_OPEN.name] = AuthWeChatOpenRequest(config, authStateCache)
            }
            
            // QQ配置
            qq?.let {
                val config = AuthConfig.builder()
                    .clientId(it.clientId)
                    .clientSecret(it.clientSecret)
                    .redirectUri(it.redirectUri)
                    .build()
                authRequests[AuthDefaultSource.QQ.name] = AuthQqRequest(config, authStateCache)
            }
            
            // 微博配置
            weibo?.let {
                val config = AuthConfig.builder()
                    .clientId(it.clientId)
                    .clientSecret(it.clientSecret)
                    .redirectUri(it.redirectUri)
                    .build()
                authRequests[AuthDefaultSource.WEIBO.name] = AuthWeiboRequest(config, authStateCache)
            }
            
            // 钉钉配置
            dingtalk?.let {
                val config = AuthConfig.builder()
                    .clientId(it.clientId)
                    .clientSecret(it.clientSecret)
                    .redirectUri(it.redirectUri)
                    .build()
                authRequests[AuthDefaultSource.DINGTALK.name] = AuthDingTalkRequest(config, authStateCache)
            }
        }
        
        return authRequests
    }
    
    /**
     * 创建AuthStateCache实例，用于缓存授权状态
     */
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

/**
 * JustAuth 配置属性
 */
@ConfigurationProperties(prefix = "just-auth")
data class JustAuthProperties(
    var github: OAuthClientConfig? = null,
    var gitee: OAuthClientConfig? = null,
    var wechat: OAuthClientConfig? = null,
    var qq: OAuthClientConfig? = null,
    var weibo: OAuthClientConfig? = null,
    var dingtalk: OAuthClientConfig? = null
)

/**
 * OAuth客户端配置
 */
data class OAuthClientConfig(
    var clientId: String = "",
    var clientSecret: String = "",
    var redirectUri: String = ""
) 