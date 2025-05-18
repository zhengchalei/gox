package com.zhengchalei.gox.modules.system.auth.config

import cn.dev33.satoken.stp.StpInterface
import com.zhengchalei.gox.modules.system.entity.*
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.springframework.stereotype.Component

/**
 * 自定义权限加载接口实现类
 */
@Component // 保证此类被 SpringBoot 扫描，完成 Sa-Token 的自定义权限验证扩展 
class StpInterfaceImpl(
    private val sqlClient: KSqlClient
) : StpInterface {
    /**
     * 返回一个账号所拥有的权限码集合
     */
    override fun getPermissionList(loginId: Any, loginType: String?): List<String> {
        return this.sqlClient.executeQuery(User::class) {
            where(table.id eq loginId as Long)
            select(table.asTableEx().roles.asTableEx().permissions.code)
        }
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    override fun getRoleList(loginId: Any, loginType: String?): List<String> {
        return this.sqlClient.executeQuery(User::class) {
            where(table.id eq loginId as Long)
            select(table.asTableEx().roles.code)
        }
    }
}
