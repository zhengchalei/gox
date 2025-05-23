package com.zhengchalei.gox.modules.system.controller

import com.zhengchalei.gox.R
import com.zhengchalei.gox.framework.config.oneIndex
import com.zhengchalei.gox.modules.system.entity.dto.*
import com.zhengchalei.gox.modules.system.service.RoleService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Tag(name = "角色管理", description = "角色相关操作")
@Validated
@RestController
@RequestMapping("/api/v1/sys/role")
class RoleController(private val roleService: RoleService) {

    @Operation(summary = "根据ID查询角色", description = "通过角色ID获取角色详细信息")
    @GetMapping("/{id}")
    fun findById(
        @Parameter(description = "角色ID", required = true)
        @PathVariable id: Long
    ): R<RoleDetailDTO> {
        val role = roleService.findById(id)
        return R.data(role)
    }

    @Operation(summary = "删除角色", description = "根据角色ID删除角色")
    @DeleteMapping("/{id}")
    fun deleteById(
        @Parameter(description = "角色ID", required = true)
        @PathVariable id: Long
    ): R<Void> {
        roleService.deleteById(id)
        return R.success()
    }

    @Operation(summary = "创建角色", description = "创建新角色")
    @PostMapping
    fun create(
        @Parameter(description = "角色信息", required = true)
        @Valid @RequestBody roleCreateDTO: RoleCreateDTO
    ): R<Void> {
        roleService.create(roleCreateDTO)
        return R.success()
    }

    @Operation(summary = "更新角色", description = "更新角色信息")
    @PutMapping
    fun update(
        @Parameter(description = "角色信息", required = true)
        @Valid @RequestBody roleUpdateDTO: RoleUpdateDTO
    ): R<Void> {
        roleService.update(roleUpdateDTO)
        return R.success()
    }

    @Operation(summary = "分页查询角色", description = "分页查询角色列表")
    @GetMapping("/page")
    fun findPage(
        @Parameter(description = "页码", required = false) @RequestParam(defaultValue = "1") currentPage: Int,
        @Parameter(description = "每页数量", required = false) @RequestParam(defaultValue = "10") pageSize: Int,
        roleSpecification: RoleSpecification,
    ): R<Page<RoleListDTO>> {
        val pageRequest: PageRequest = PageRequest.of(currentPage, pageSize).oneIndex()
        val pageResult = roleService.findPage(pageRequest, roleSpecification)
        return R.data(pageResult)
    }
}