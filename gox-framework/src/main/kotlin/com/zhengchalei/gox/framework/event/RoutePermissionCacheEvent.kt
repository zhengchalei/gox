package com.zhengchalei.gox.framework.event

import org.springframework.context.ApplicationEvent

/**
 * 路由权限缓存事件基类
 */
sealed class RoutePermissionCacheEvent(source: Any) : ApplicationEvent(source)

/**
 * 清除指定角色ID的路由权限缓存事件
 */
class RoleRoutePermissionCacheInvalidateEvent(source: Any, val roleId: Long) : RoutePermissionCacheEvent(source)

/**
 * 清除所有路由权限缓存事件
 */
class AllRoutePermissionCacheInvalidateEvent(source: Any) : RoutePermissionCacheEvent(source) 