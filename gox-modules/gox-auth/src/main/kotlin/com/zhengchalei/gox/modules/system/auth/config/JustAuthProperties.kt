package com.zhengchalei.gox.modules.system.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/** JustAuth配置属性 */
@Component
@ConfigurationProperties(prefix = "gox.oauth2")
class JustAuthProperties {
    // 旧版Map格式配置（保持兼容）
    var github: SocialProperties? = null
    var gitee: SocialProperties? = null
    var wechat: SocialProperties? = null
    var qq: SocialProperties? = null
    var weibo: SocialProperties? = null
    var dingtalk: SocialProperties? = null
}

/** 社交登录平台配置 */
class SocialProperties() {

    var clientId: String? = null
    var clientSecret: String? = null
    var redirectUri: String? = null

    fun isEmpty(): Boolean =
        clientId?.isBlank() == true ||
                clientSecret?.isBlank() == true ||
                redirectUri?.isBlank() == true
}
