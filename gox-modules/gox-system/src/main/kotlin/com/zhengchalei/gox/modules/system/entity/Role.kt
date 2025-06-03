package com.zhengchalei.gox.modules.system.entity

import org.babyfish.jimmer.sql.*
import java.time.LocalDateTime
import com.zhengchalei.gox.modules.system.entity.BaseEntity

/**
 * 系统角色
 *
 * @author zhengchalei
 */
@Entity
@Table(name = "sys_role")
interface Role : BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long

    @Key
    val name: String

    @Key
    val code: String

    val description: String?

    val enabled: Boolean

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

}