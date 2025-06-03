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

    val createdTime: LocalDateTime

    val updatedTime: LocalDateTime

    val createdBy: String?

    val modifiedBy: String?
}