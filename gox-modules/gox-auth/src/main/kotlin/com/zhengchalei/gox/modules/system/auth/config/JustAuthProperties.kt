package com.zhengchalei.gox.modules.system.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Component

/** JustAuth 配置 */
@PropertySource("classpath:application-auth.yml")
@Component
@ConfigurationProperties(prefix = "gox.social")
class JustAuthProperties {
    /** GitHub配置 */
    var github: Map<String, String> = mutableMapOf()

    /** Gitee配置 */
    var gitee: Map<String, String> = mutableMapOf()

    /** 微信配置 */
    var wechat: Map<String, String> = mutableMapOf()

    /** QQ配置 */
    var qq: Map<String, String> = mutableMapOf()

    /** 微博配置 */
    var weibo: Map<String, String> = mutableMapOf()

    /** 钉钉配置 */
    var dingtalk: Map<String, String> = mutableMapOf()
}