package com.zhengchalei.gox.modules.iot.device.entity

import com.zhengchalei.gox.modules.iot.device.entity.BaseEntity


/**
 * 设备
 *
 * @author zhengchalei
 */
@Entity
@Table(name = "iot_device")
interface Device : BaseEntity {

    /**
     * 设备编号
     */
    val code: String

    /**
     * 设备名称
     */
    val name: String

    /**
     * 设备类型
     */
    val type: String

    /**
     * 设备状态
     */
    val status: DeviceStatus

}

enum class DeviceStatus {
    /**
     * 在线
     */
    ONLINE,
    /**
     * 离线
     */
    OFFLINE
}
