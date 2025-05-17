package com.zhengchalei.gox.modules.system.auth.interceptor

import cn.dev33.satoken.stp.StpUtil
import com.zhengchalei.gox.framework.event.AllRoutePermissionCacheInvalidateEvent
import com.zhengchalei.gox.framework.event.RoleRoutePermissionCacheInvalidateEvent
import com.zhengchalei.gox.modules.system.entity.dto.RoutePermissionDetailDTO
import com.zhengchalei.gox.modules.system.entity.dto.UserDetailDTO
import com.zhengchalei.gox.modules.system.service.RoutePermissionService
import com.zhengchalei.gox.modules.system.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.http.HttpStatus
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
 * 3. 使用事件机制更新缓存，确保权限更新及时生效
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
     * 监听角色路由权限缓存失效事件
     */
    @EventListener
    fun handleRoleRoutePermissionCacheInvalidateEvent(event: RoleRoutePermissionCacheInvalidateEvent) {
        logger.info("接收到角色路由权限缓存失效事件，角色ID: {}", event.roleId)
        invalidateCachesForRoleId(event.roleId)
    }

    /**
     * 监听所有路由权限缓存失效事件
     */
    @EventListener
    fun handleAllRoutePermissionCacheInvalidateEvent(event: AllRoutePermissionCacheInvalidateEvent) {
        logger.info("接收到所有路由权限缓存失效事件")
        invalidateAllCaches()
    }

    /**
     * 使指定角色ID相关的缓存失效
     * 当某个角色的权限分配发生变化或角色被删除时调用
     */
    private fun invalidateCachesForRoleId(roleId: Long) {
        logger.info("正在失效角色ID: {} 的相关缓存", roleId)
        routePermissionCache.remove(roleId)

        // 清理pathMatchCache中所有可能包含该roleId的条目
        // Key格式: "roleId1,roleId2,...:path:method"
        val roleIdString = roleId.toString()
        pathMatchCache.keys.removeIf { key ->
            val rolesPartEndIndex = key.lastIndexOf(':')
            if (rolesPartEndIndex == -1) return@removeIf false // 无效的key格式
            val rolesPartStartIndex = key.lastIndexOf(':', rolesPartEndIndex -1)
            if (rolesPartStartIndex == -1) return@removeIf false // 无效的key格式

            val rolesPart = key.substring(rolesPartStartIndex + 1, rolesPartEndIndex)
            val roleIdsInKey = rolesPart.split(',')
            roleIdsInKey.contains(roleIdString)
        }
        logger.info("角色ID: {} 的相关缓存已失效", roleId)
    }

    /**
     * 使所有路由权限相关的缓存失效
     * 当路由权限定义本身发生变化或进行批量操作时调用
     */
    private fun invalidateAllCaches() {
        logger.info("正在失效所有路由权限缓存")
        routePermissionCache.clear()
        pathMatchCache.clear()
        logger.info("所有路由权限缓存已失效")
    }

    /**
     * 将数据库中的路由权限信息转换为路由权限模式对象
     */
    private fun convertToPattern(routePermissionDTO: RoutePermissionDetailDTO): RoutePermissionPattern {
        // 将通配符路径转换为正则表达式模式
        var regexPath = routePermissionDTO.path
        
        // 首先对特殊字符进行转义，确保例如 . 这样的字符被正确处理为字面值
        regexPath = regexPath.replace(".", "\\.")
                            .replace("(", "\\(")
                            .replace(")", "\\)")
                            .replace("[", "\\[")
                            .replace("]", "\\]")
                            .replace("{", "\\{")
                            .replace("}", "\\}")
                            .replace("+", "\\+")
                            .replace("^", "\\^")
                            .replace("$", "\\$")
                            .replace("|", "\\|")
        
        // 然后处理通配符
        regexPath = regexPath.replace("/**", "(/.*)?")  // /**匹配任意多级路径，包括空路径
                            .replace("/*", "(/[^/]+)")  // /*匹配单级非空路径

        val finalPattern = "^${regexPath}$"
        logger.debug("转换路径 '{}' 到正则表达式: '{}'", routePermissionDTO.path, finalPattern)
        
        val pattern = Pattern.compile(finalPattern)
        
        // 测试匹配示例
        if (routePermissionDTO.path.contains("/**")) {
            val testPaths = listOf(
                routePermissionDTO.path.replace("/**", ""),  // 基本路径
                routePermissionDTO.path.replace("/**", "/123"),  // 单级数字ID
                routePermissionDTO.path.replace("/**", "/test"),  // 单级字符串
                routePermissionDTO.path.replace("/**", "/test/123")  // 多级路径
            )
            
            for (testPath in testPaths) {
                val matches = pattern.matcher(testPath).matches()
                logger.debug("测试路径 '{}' 与模式 '{}' 匹配结果: {}", testPath, finalPattern, matches)
            }
        }

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
        logger.debug("检查用户权限 - ID: {}, 用户名: {}, 角色: {}, 请求路径: {}, 请求方法: {}", user.id, user.username, roleIds, path, method)

        // 优先从缓存中获取结果
        val cacheKey = "${roleIds.sorted().joinToString(",")}:$path:$method"
        val cachedResult = pathMatchCache[cacheKey]
        if (cachedResult != null) {
            logger.debug("从缓存获取权限检查结果: {}", cachedResult)
            return cachedResult
        }

        // 获取用户所有角色对应的路由权限
        val permissions = loadUserRoutePermissions(roleIds)
        logger.debug("已加载用户 ID {} 的权限: {}", user.id, permissions.map { "ID: ${it.id}, 路径: ${it.path}, 方法: ${it.method}" })

        // 检查是否有匹配的路由权限
        var hasPermission = false
        for (permission in permissions) {
            val methodMatches = permission.method.equals(method, ignoreCase = true)
            val pathMatches = permission.pathPattern.matcher(path).matches()
            
            logger.debug("权限检查 - ID: {}, 路径: {}, 方法: {}, 正则: {} - 方法匹配: {}, 路径匹配: {}", 
                        permission.id, permission.path, permission.method, permission.pathPattern.pattern(), 
                        methodMatches, pathMatches)
            
            if (methodMatches && pathMatches) {
                hasPermission = true
                logger.debug("找到匹配的权限: ID={}, 路径={}, 方法={}", permission.id, permission.path, permission.method)
                break
            }
        }

        // 缓存结果
        pathMatchCache[cacheKey] = hasPermission
        logger.debug("权限检查结果: {}, 已缓存", hasPermission)

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
                // logger.debug("User not logged in, skipping route permission check for: {} {}", method, path) // 可选日志
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
            response.contentType = "application/json"
            response.characterEncoding = "UTF-8"
            response.writer.write("无权限访问")
            return false

        } catch (e: Exception) {
            logger.error("路由权限检查异常", e)
            response.status = HttpStatus.INTERNAL_SERVER_ERROR.value()
            response.contentType = "application/json"
            response.characterEncoding = "UTF-8"
            response.writer.write("服务器内部错误")
            return false
        }
    }
} 