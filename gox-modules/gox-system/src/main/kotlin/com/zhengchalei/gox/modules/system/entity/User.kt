package com.zhengchalei.gox.modules.system.entity

import org.babyfish.jimmer.sql.*
import java.time.LocalDateTime

@Entity
@Table(name = "sys_user")
interface User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long

    @Key
    val username: String

    val nickname: String

    val avatar: String

    val email: String?

    val phone: String?

    val password: String

    val enabled: Boolean

    val createdTime: LocalDateTime

    val updatedTime: LocalDateTime

    @ManyToMany
    @JoinTable(
        name = "sys_user_role",
        joinColumnName = "user_id",
        inverseJoinColumnName = "role_id"
    )
    val roles: List<Role>

    @IdView("roles")
    val roleIds: List<Long>
}