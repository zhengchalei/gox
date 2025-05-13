package com.zhengchalei.gox.modules.system.auth.service.impl

import com.zhengchalei.gox.modules.system.auth.dto.LoginRequest
import com.zhengchalei.gox.modules.system.auth.dto.LoginResponse
import com.zhengchalei.gox.modules.system.auth.service.AuthService
import com.zhengchalei.gox.modules.system.entity.User
import com.zhengchalei.gox.modules.system.entity.username
import com.zhengchalei.gox.util.PasswordUtil
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthServiceImpl(
    private val sqlClient: KSqlClient
) : AuthService {

    companion object {
        // Token Map
        val tokenMap = mutableMapOf<String, User>()
    }

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun login(request: LoginRequest): LoginResponse {

        val user: User = this.sqlClient.createQuery(User::class) {
            where(table.username eq request.username)
            select(table)
        }.fetchOneOrNull() ?: throw IllegalArgumentException("用户不存在")

        if (!PasswordUtil.matches(request.password, user.password)) {
            logger.warn("密码错误: {}", user.username)
            throw IllegalArgumentException("密码错误")
        }

        if (!user.enabled) {
            logger.warn("用户被禁用: {}", user.username)
            throw IllegalArgumentException("用户被禁用")
        }

        val loginResponse = LoginResponse(
            token = UUID.randomUUID().toString(),
            username = request.username
        )
        tokenMap[loginResponse.token] = user
        return loginResponse
    }
} 