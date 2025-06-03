package com.zhengchalei.gox.modules.iot.device.entity

import com.zhengchalei.gox.framework.jimmer.entity.BaseEntity
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.EnumType
import org.babyfish.jimmer.sql.Table


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

@EnumType(value = EnumType.Strategy.NAME)
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
