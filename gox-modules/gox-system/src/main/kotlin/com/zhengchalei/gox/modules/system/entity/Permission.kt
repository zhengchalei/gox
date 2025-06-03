package com.zhengchalei.gox.modules.system.entity

import org.babyfish.jimmer.sql.*
import java.time.LocalDateTime
import com.zhengchalei.gox.modules.system.entity.BaseEntity

/**
 * 系统权限
 * 
 * @author zhengchalei
 */
@Entity
@Table(name = "sys_permission")
interface Permission : BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long

    val name: String

    @Key
    val code: String

    val description: String?

    @ManyToMany(mappedBy = "permissions")
    val roles: List<Role>

    @IdView("roles")
    val roleIds: List<Long>


}