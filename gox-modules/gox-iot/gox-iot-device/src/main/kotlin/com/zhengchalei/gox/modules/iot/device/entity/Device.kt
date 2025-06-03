package com.zhengchalei.gox.modules.iot.device.entity

import com.zhengchalei.gox.framework.jimmer.entity.BaseEntity
import org.babyfish.jimmer.sql.*
import java.time.LocalDateTime

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
     * 设备描述
     */
    val description: String?
    /**
     * 设备状态
     */
    val online: Boolean
}
