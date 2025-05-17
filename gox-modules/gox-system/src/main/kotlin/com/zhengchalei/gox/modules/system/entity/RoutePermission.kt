package com.zhengchalei.gox.modules.system.entity

import org.babyfish.jimmer.sql.*
import java.time.LocalDateTime

@Entity
@Table(name = "sys_route_permission")
interface RoutePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long

    @Key
    val path: String

    @Key
    val method: String

    val description: String?

    val createdTime: LocalDateTime

    val updatedTime: LocalDateTime

    @ManyToMany(mappedBy = "routePermissions")
    val roles: List<Role>

    @IdView("roles")
    val roleIds: List<Long>
} 