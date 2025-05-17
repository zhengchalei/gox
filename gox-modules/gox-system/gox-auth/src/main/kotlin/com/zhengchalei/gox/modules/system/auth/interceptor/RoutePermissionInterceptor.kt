package com.zhengchalei.gox.modules.system.interceptor

import cn.dev33.satoken.stp.StpUtil
import com.zhengchalei.gox.modules.system.entity.dto.RoutePermissionDetailDTO
import com.zhengchalei.gox.modules.system.entity.dto.UserDetailDTO
import com.zhengchalei.gox.modules.system.service.RoutePermissionService
import com.zhengchalei.gox.modules.system.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

/**
 * 路由权限拦截器
 *
 * 性能优化设计：
 * 1. 使用本地缓存存储路由权限匹配规则
 * 2. 使用正则表达式预编译提高匹配效率
 * 3. 定时刷新缓存，确保权限更新及时生效
 * 4. 使用路径匹配优化算法减少查询次数
 */
@Component
class RoutePermissionInterceptor(
    private val userService: UserService,
    private val routePermissionService: RoutePermissionService
) : HandlerInterceptor {

    private val logger = LoggerFactory.getLogger(RoutePermissionInterceptor::class.java)

    // 路由权限缓存，按角色ID分组
    private val routePermissionCache = ConcurrentHashMap<Long, List<RoutePermissionPattern>>()

    // 路径匹配缓存，减少重复正则匹配
    private val pathMatchCache = ConcurrentHashMap<String, Boolean>()

    /**
     * 路由权限模式类，包含预编译的正则表达式
     */
    data class RoutePermissionPattern(
        val id: Long,
        val path: String,
        val method: String,
        val pathPattern: Pattern
    )

    /**
     * 定时刷新缓存 (每5分钟刷新一次)
     */
    @Scheduled(fixedRate = 300000)
    fun refreshCache() {
        logger.info("开始刷新路由权限缓存")
        routePermissionCache.clear()
        pathMatchCache.clear()
        logger.info("路由权限缓存已刷新")
    }

    /**
     * 将数据库中的路由权限信息转换为路由权限模式对象
     */
    private fun convertToPattern(routePermissionDTO: RoutePermissionDetailDTO): RoutePermissionPattern {
        // 将通配符路径转换为正则表达式模式
        val regexPath = routePermissionDTO.path
            .replace("/**", "(/.*)?")  // /**匹配任意多级路径
            .replace("/*", "(/[^/]*)?") // /*匹配单级路径
            .replace(".", "\\.")        // 对.进行转义

        val pattern = Pattern.compile("^${regexPath}$")

        return RoutePermissionPattern(
            id = routePermissionDTO.id,
            path = routePermissionDTO.path,
            method = routePermissionDTO.method,
            pathPattern = pattern
        )
    }

    /**
     * 加载用户的路由权限
     */
    private fun loadUserRoutePermissions(roleIds: List<Long>): List<RoutePermissionPattern> {
        if (roleIds.isEmpty()) {
            return emptyList()
        }

        val result = mutableListOf<RoutePermissionPattern>()

        for (roleId in roleIds) {
            val cachedPermissions = routePermissionCache[roleId]
            if (cachedPermissions != null) {
                result.addAll(cachedPermissions)
                continue
            }

            // 角色对应的路由权限不在缓存中，从数据库加载
            val rolePermissions = routePermissionService.findByRoleIds(listOf(roleId))
                .map { convertToPattern(it) }

            routePermissionCache[roleId] = rolePermissions
            result.addAll(rolePermissions)
        }

        return result.distinctBy { it.id }
    }

    /**
     * 检查用户是否有权限访问指定路径
     */
    private fun hasPermission(user: UserDetailDTO, path: String, method: String): Boolean {
        val roleIds = user.roleIds

        // 优先从缓存中获取结果
        val cacheKey = "${roleIds.sorted().joinToString(",")}:$path:$method"
        val cachedResult = pathMatchCache[cacheKey]
        if (cachedResult != null) {
            return cachedResult
        }

        // 获取用户所有角色对应的路由权限
        val permissions = loadUserRoutePermissions(roleIds)

        // 检查是否有匹配的路由权限
        val hasPermission = permissions.any {
            it.method.equals(method, ignoreCase = true) &&
                    it.pathPattern.matcher(path).matches()
        }

        // 缓存结果
        pathMatchCache[cacheKey] = hasPermission

        return hasPermission
    }

    /**
     * 在请求处理之前进行调用
     */
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        // 如果不是处理方法，直接放行
        if (handler !is HandlerMethod) {
            return true
        }

        val path = request.requestURI
        val method = request.method

        logger.debug("拦截请求: {} {}", method, path)

        try {
            // 检查用户是否登录， 如果没有登陆就不管这里了， 应该交由认证拦截器处理， 这里只负责授权， 不负责认证
            val login: Boolean = StpUtil.isLogin()
            if (!login) {
                return true
            }

            val userId: Long = StpUtil.getLoginIdAsLong()

            val user = userService.findById(userId)

            // 管理员角色直接放行
            if (user.roles.any { it.code == "ADMIN" }) {
                return true
            }

            // 检查权限
            if (hasPermission(user, path, method)) {
                return true
            }

            // 无权限访问
            logger.warn("用户 id:[{}], username:[{}] 无权限访问: {} {}", user.id, user.username, method, path)
            response.status = HttpStatus.FORBIDDEN.value()
            response.writer.write("无权限访问")
            return false

        } catch (e: Exception) {
            logger.error("路由权限检查异常", e)
            response.status = HttpStatus.INTERNAL_SERVER_ERROR.value()
            response.writer.write("服务器内部错误")
            return false
        }
    }
} 