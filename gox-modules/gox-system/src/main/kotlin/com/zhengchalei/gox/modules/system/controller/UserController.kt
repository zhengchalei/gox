package com.zhengchalei.gox.modules.system.controller

import com.zhengchalei.gox.R
import com.zhengchalei.gox.framework.config.oneIndex
import com.zhengchalei.gox.modules.system.entity.dto.*
import com.zhengchalei.gox.modules.system.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Tag(name = "用户管理", description = "用户相关操作")
@Validated
@RestController
@RequestMapping("/api/v1/sys/user")
class UserController(private val userService: UserService) {

    @Operation(summary = "根据用户名查询用户", description = "通过用户名获取用户详细信息")
    @GetMapping(value = ["/username/{username}"])
    fun findByUsername(
        @Parameter(description = "用户名", required = true)
        @PathVariable username: String
    ): R<UserDetailDTO> {
        val user = userService.findByUsername(username)
        return R.data(user)
    }

    @Operation(summary = "根据ID查询用户", description = "通过用户ID获取用户详细信息")
    @GetMapping(value = ["/{id}"])
    fun findById(
        @Parameter(description = "用户ID", required = true)
        @PathVariable id: Long
    ): R<UserDetailDTO> {
        val user = userService.findById(id)
        return R.data(user)
    }

    @Operation(summary = "删除用户", description = "根据用户ID删除用户")
    @DeleteMapping(value = ["/{id}"])
    fun deleteById(
        @Parameter(description = "用户ID", required = true)
        @PathVariable id: Long
    ): R<Void> {
        userService.deleteById(id)
        return R.success()
    }

    @Operation(summary = "创建用户", description = "创建新用户")
    @PostMapping()
    fun create(
        @Parameter(description = "用户信息", required = true)
        @Valid @RequestBody userCreateDTO: UserCreateDTO
    ): R<Void> {
        userService.create(userCreateDTO)
        return R.success()
    }

    @Operation(summary = "更新用户", description = "更新用户信息")
    @PutMapping()
    fun update(
        @Parameter(description = "用户信息", required = true)
        @Valid @RequestBody userUpdateDTO: UserUpdateDTO
    ): R<Void> {
        userService.update(userUpdateDTO)
        return R.success()
    }

    @Operation(summary = "修改用户角色", description = "修改用户角色")
    @PostMapping("/role")
    fun updateUserRole(
        @Parameter(description = "用户角色信息", required = true)
        @Valid @RequestBody userRoleUpdateDTO: UserRoleUpdateDTO
    ): R<Void> {
        userService.updateUserRole(userRoleUpdateDTO)
        return R.success()
    }

    @Operation(summary = "分页查询用户", description = "分页查询用户列表")
    @GetMapping(value = ["/page"])
    fun findPage(
        @Parameter(description = "页码", required = false) @RequestParam(defaultValue = "1") currentPage: Int,
        @Parameter(description = "每页数量", required = false) @RequestParam(defaultValue = "10") pageSize: Int,
        userSpecification: UserSpecification,
    ): R<Page<UserListDTO>> {
        val pageRequest: PageRequest = PageRequest.of(currentPage, pageSize).oneIndex()
        val pageResult = userService.findPage(pageRequest, userSpecification)
        return R.data(pageResult)
    }
}
