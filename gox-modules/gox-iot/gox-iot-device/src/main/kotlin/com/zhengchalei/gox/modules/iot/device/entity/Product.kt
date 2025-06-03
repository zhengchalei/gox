package com.zhengchalei.gox.modules.iot.device.entity

import com.zhengchalei.gox.framework.jimmer.entity.BaseEntity
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Table

/**
 * 产品
 *
 * @author zhengchalei
 */
@Entity
@Table(name = "iot_product")
interface Product : BaseEntity {

    val name: String

    val code: String

    val description: String?

}