package com.zhengchalei.gox.modules.system.entity

import com.zhengchalei.gox.framework.jimmer.entity.BaseEntity
import org.babyfish.jimmer.sql.*

/**
 * 系统权限
 * 
 * @author zhengchalei
 */
@Entity
@Table(name = "sys_permission")
interface Permission : BaseEntity {

    val name: String

    @Key
    val code: String

    val description: String?

    @ManyToMany(mappedBy = "permissions")
    val roles: List<Role>

    @IdView("roles")
    val roleIds: List<Long>


}