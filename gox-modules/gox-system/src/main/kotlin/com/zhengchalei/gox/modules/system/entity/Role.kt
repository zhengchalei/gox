package com.zhengchalei.gox.modules.system.entity

import org.babyfish.jimmer.sql.*
import java.time.LocalDateTime

@Entity
@Table(name = "sys_role")
interface Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long

    @Key
    val name: String

    @Key
    val code: String

    val description: String?

    val enabled: Boolean

    val createdTime: LocalDateTime

    val updatedTime: LocalDateTime

    @ManyToMany(mappedBy = "roles")
    val users: List<User>

    @IdView("users")
    val userIds: List<Long>

    @ManyToMany
    @JoinTable(
        name = "sys_role_permission",
        joinColumnName = "role_id",
        inverseJoinColumnName = "permission_id"
    )
    val permissions: List<Permission>

    @IdView("permissions")
    val permissionIds: List<Long>
    
    @ManyToMany
    @JoinTable(
        name = "sys_role_route",
        joinColumnName = "role_id",
        inverseJoinColumnName = "route_permission_id"
    )
    val routePermissions: List<RoutePermission>

    @IdView("routePermissions")
    val routePermissionIds: List<Long>
}