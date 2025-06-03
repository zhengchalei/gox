package com.zhengchalei.gox.modules.iot.device.entity
import org.babyfish.jimmer.sql.*
import java.time.LocalDateTime
import com.zhengchalei.gox.modules.iot.device.entity.BaseEntity
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