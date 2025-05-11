package com.zhengchalei.gox.modules.system.entity

import org.babyfish.jimmer.sql.*
import java.time.LocalDateTime

@Entity
@Table(name = "sys_user")
interface User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long

    val username: String

    val password: String

    val enabled: Boolean

    val createdTime: LocalDateTime

    val updatedTime: LocalDateTime
}