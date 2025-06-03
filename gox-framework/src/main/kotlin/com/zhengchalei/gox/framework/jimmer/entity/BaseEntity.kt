package com.zhengchalei.gox.framework.jimmer.entity

import java.time.LocalDateTime
import org.babyfish.jimmer.sql.MappedSuperclass
import org.babyfish.jimmer.sql.Id
import org.babyfish.jimmer.sql.GeneratedValue
import org.babyfish.jimmer.sql.GenerationType


@MappedSuperclass
interface BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long

    // 创建时间
    val createdTime: LocalDateTime

    // 最后修改时间
    val modifiedTime: LocalDateTime?

    // 创建人-可为空
    val createdBy: Long?

    // 最后修改人-可为空
    val modifiedBy: Long?
}