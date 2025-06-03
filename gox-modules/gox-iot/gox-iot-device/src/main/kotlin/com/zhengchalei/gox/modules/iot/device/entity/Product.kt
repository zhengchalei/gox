package com.zhengchalei.gox.modules.iot.device.entity

import com.zhengchalei.gox.framework.jimmer.entity.BaseEntity
import org.babyfish.jimmer.sql.*
import java.time.LocalDateTime

@Entity
@Table(name = "iot_product")
interface Product : BaseEntity {
    /**
     * 产品编号
     */
    val code: String
    /**
     * 产品名称
     */
    val name: String
    /**
     * 产品描述
     */
    val description: String?
    /**
     * 产品状态
     */
    val status: Boolean
}
