package com.zhengchalei.gox.modules.iot.device.entity

import org.babyfish.jimmer.sql.*

/**
 * MQTT访问控制
 *
 * @author zhengchalei
 */
@Entity
@Table(name = "mqtt_acl")
interface MqttAcl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long

    /**
     * IP地址
     */
    val ipaddress: String

    /**
     * 用户名
     */
    val username: String

    /**
     * 客户端ID
     */
    @Column(name = "clientid")
    val clientId: String

    /**
     * 操作
     */
    val action: MqttAclAction?

    /**
     * 权限
     */
    val permission: MqttAclPermission?
}

@EnumType(value = EnumType.Strategy.NAME)
enum class MqttAclAction {
    publish,
    subscribe,
    all
}

@EnumType(value = EnumType.Strategy.NAME)
enum class MqttAclPermission {
    allow,
    deny
}
