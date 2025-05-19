package com.zhengchalei.gox.modules.system.controller

import com.zhengchalei.gox.R
import com.zhengchalei.gox.framework.config.oneIndex
import com.zhengchalei.gox.modules.system.entity.dto.*
import com.zhengchalei.gox.modules.system.service.RoutePermissionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Tag(name = "路由权限管理", description = "路由权限相关操作")
@Validated
@RestController
@RequestMapping("/api/v1/sys/route-permission")
class RoutePermissionController(private val routePermissionService: RoutePermissionService) {

    @Operation(summary = "根据ID查询路由权限", description = "通过路由权限ID获取路由权限详细信息")
    @GetMapping("/{id}")
    fun findById(
        @Parameter(description = "路由权限ID", required = true)
        @PathVariable id: Long
    ): R<RoutePermissionDetailDTO> {
        val routePermission = routePermissionService.findById(id)
        return R.data(routePermission)
    }

    @Operation(summary = "删除路由权限", description = "根据路由权限ID删除路由权限")
    @DeleteMapping("/{id}")
    fun deleteById(
        @Parameter(description = "路由权限ID", required = true)
        @PathVariable id: Long
    ): R<Void> {
        routePermissionService.deleteById(id)
        return R.success()
    }

    @Operation(summary = "创建路由权限", description = "创建新路由权限")
    @PostMapping
    fun create(
        @Parameter(description = "路由权限信息", required = true)
        @Valid @RequestBody routePermissionCreateDTO: RoutePermissionCreateDTO
    ): R<Void> {
        routePermissionService.create(routePermissionCreateDTO)
        return R.success()
    }

    @Operation(summary = "更新路由权限", description = "更新路由权限信息")
    @PutMapping
    fun update(
        @Parameter(description = "路由权限信息", required = true)
        @Valid @RequestBody routePermissionUpdateDTO: RoutePermissionUpdateDTO
    ): R<Void> {
        routePermissionService.update(routePermissionUpdateDTO)
        return R.success()
    }

    @Operation(summary = "分页查询路由权限", description = "分页查询路由权限列表")
    @GetMapping("/page")
    fun findPage(
        @Parameter(description = "页码", required = false)
        @RequestParam(defaultValue = "1") page: Int,
        @Parameter(description = "每页数量", required = false)
        @RequestParam(defaultValue = "10") size: Int,
        routePermissionSpecification: RoutePermissionSpecification,
    ): R<Page<RoutePermissionListDTO>> {
        val pageRequest: PageRequest = PageRequest.of(page, size).oneIndex()
        val pageResult = routePermissionService.findPage(pageRequest, routePermissionSpecification)
        return R.data(pageResult)
    }

    @Operation(summary = "根据路径和方法查询路由权限", description = "根据路径和HTTP方法查询路由权限")
    @GetMapping("/path")
    fun findByPathAndMethod(
        @Parameter(description = "路径", required = true)
        @RequestParam path: String,
        @Parameter(description = "HTTP方法", required = true)
        @RequestParam method: String
    ): R<RoutePermissionDetailDTO?> {
        val routePermission = routePermissionService.findByPathAndMethod(path, method)
        return R.data(routePermission)
    }

    @Operation(summary = "根据角色ID查询路由权限", description = "根据角色ID列表查询对应的路由权限")
    @GetMapping("/roles")
    fun findByRoleIds(
        @Parameter(description = "角色ID列表", required = true)
        @RequestParam roleIds: List<Long>
    ): R<List<RoutePermissionDetailDTO>> {
        val routePermissions = routePermissionService.findByRoleIds(roleIds)
        return R.data(routePermissions)
    }
} 