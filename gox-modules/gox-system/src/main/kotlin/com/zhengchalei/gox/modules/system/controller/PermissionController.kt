package com.zhengchalei.gox.modules.system.controller

import com.zhengchalei.gox.framework.config.oneIndex
import com.zhengchalei.gox.modules.system.entity.dto.*
import com.zhengchalei.gox.modules.system.service.PermissionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "权限管理", description = "权限相关操作")
@RestController
@RequestMapping("/api/v1/sys/permission")
class PermissionController(private val permissionService: PermissionService) {

    @Operation(summary = "根据ID查询权限", description = "通过权限ID获取权限详细信息")
    @GetMapping("/{id}")
    fun findById(
        @Parameter(description = "权限ID", required = true)
        @PathVariable id: Long
    ): ResponseEntity<PermissionDetailDTO> {
        val permission = permissionService.findById(id)
        return ResponseEntity.ok(permission)
    }

    @Operation(summary = "删除权限", description = "根据权限ID删除权限")
    @DeleteMapping("/{id}")
    fun deleteById(
        @Parameter(description = "权限ID", required = true)
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        permissionService.deleteById(id)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "创建权限", description = "创建新权限")
    @PostMapping
    fun create(
        @Parameter(description = "权限信息", required = true)
        @Valid @RequestBody permissionCreateDTO: PermissionCreateDTO
    ): ResponseEntity<Void> {
        permissionService.create(permissionCreateDTO)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "更新权限", description = "更新权限信息")
    @PutMapping
    fun update(
        @Parameter(description = "权限信息", required = true)
        @Valid @RequestBody permissionUpdateDTO: PermissionUpdateDTO
    ): ResponseEntity<Void> {
        permissionService.update(permissionUpdateDTO)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "分页查询权限", description = "分页查询权限列表")
    @GetMapping("/page")
    fun findPage(
        @Parameter(description = "页码", required = false)
        @RequestParam(defaultValue = "1") page: Int,
        @Parameter(description = "每页数量", required = false)
        @RequestParam(defaultValue = "10") size: Int,
        permissionSpecification: PermissionSpecification,
    ): ResponseEntity<Page<PermissionListDTO>> {
        val pageRequest: PageRequest = PageRequest.of(page, size).oneIndex()
        val pageResult = permissionService.findPage(pageRequest, permissionSpecification)
        return ResponseEntity.ok(pageResult)
    }
}