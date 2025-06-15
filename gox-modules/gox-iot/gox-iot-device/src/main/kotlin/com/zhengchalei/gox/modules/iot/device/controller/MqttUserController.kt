package com.zhengchalei.gox.modules.iot.device.controller

import com.zhengchalei.gox.framework.config.oneIndex
import com.zhengchalei.gox.modules.iot.device.entity.dto.*
import com.zhengchalei.gox.modules.iot.device.service.MqttUserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * MQTT用户Controller
 *
 * @author zhengchalei
 */
@Tag(name = "MQTT用户管理", description = "MQTT用户相关操作")
@Validated
@RestController
@RequestMapping("/api/iot/mqtt-user")
class MqttUserController(private val service: MqttUserService) {

    @Operation(summary = "根据ID查询MQTT用户", description = "通过MQTT用户ID获取MQTT用户详细信息")
    @GetMapping(value = ["/{id}"])
    fun findById(
        @Parameter(description = "MqttUserID", required = true)
        @PathVariable id: Long
    ): ResponseEntity<MqttUserDetailDTO> {
        val result = service.findById(id)
        return ResponseEntity.ok(result)
    }

    @Operation(summary = "删除MQTT用户", description = "根据MQTT用户ID删除MQTT用户")
    @DeleteMapping(value = ["/{id}"])
    fun deleteById(
        @Parameter(description = "MqttUserID", required = true)
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        service.deleteById(id)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "创建MQTT用户", description = "创建新MQTT用户")
    @PostMapping(value = [""])
    fun create(
        @Parameter(description = "MqttUser信息", required = true)
        @Valid @RequestBody dto: MqttUserCreateDTO
    ): ResponseEntity<Void> {
        service.create(dto)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "更新MQTT用户", description = "更新MQTT用户信息")
    @PutMapping(value = [""])
    fun update(
        @Parameter(description = "MqttUser信息", required = true)
        @Valid @RequestBody dto: MqttUserUpdateDTO
    ): ResponseEntity<Void> {
        service.update(dto)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "分页查询MQTT用户", description = "分页查询MQTT用户列表")
    @GetMapping(value = ["/page"])
    fun findPage(
        @Parameter(description = "页码", required = false) @RequestParam(defaultValue = "1") currentPage: Int,
        @Parameter(description = "每页数量", required = false) @RequestParam(defaultValue = "10") pageSize: Int,
        specification: MqttUserSpecification
    ): ResponseEntity<Page<MqttUserListDTO>> {
        val pageRequest = PageRequest.of(currentPage, pageSize).oneIndex()
        val pageResult = service.findPage(pageRequest, specification)
        return ResponseEntity.ok(pageResult)
    }
}
