package com.zhengchalei.gox.modules.iot.device.entity

import org.babyfish.jimmer.sql.*

/**
 * MQTT用户
 *
 * @author zhengchalei
 */
@Entity
@Table(name = "mqtt_user")
interface MqttUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long

    /**
     * 用户名
     */
    @Key
    val username: String
    /**
     * 密码
     */
    val passwordHash: String
    /**
     * 盐
     */
    val salt: String

    /**
     * 是否超级用户
     */
    val isSuperuser: Boolean
}
