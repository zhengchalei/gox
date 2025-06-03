package com.zhengchalei.gox.modules.iot.device.controller

import com.zhengchalei.gox.framework.config.oneIndex
import com.zhengchalei.gox.modules.iot.device.entity.dto.*
import com.zhengchalei.gox.modules.iot.device.service.DeviceService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Tag(name = "Device管理", description = "Device相关操作")
@Validated
@RestController
@RequestMapping("/api/sys/device")
class DeviceController(private val service: DeviceService) {

    @Operation(summary = "根据ID查询Device", description = "通过DeviceID获取Device详细信息")
    @GetMapping(value = ["/{id}"])
    fun findById(
        @Parameter(description = "DeviceID", required = true)
        @PathVariable id: Long
    ): ResponseEntity<DeviceDetailDTO> {
        val result = service.findById(id)
        return ResponseEntity.ok(result)
    }

    @Operation(summary = "删除Device", description = "根据DeviceID删除Device")
    @DeleteMapping(value = ["/{id}"])
    fun deleteById(
        @Parameter(description = "DeviceID", required = true)
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        service.deleteById(id)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "创建Device", description = "创建新Device")
    @PostMapping(value = ["/"])
    fun create(
        @Parameter(description = "Device信息", required = true)
        @Valid @RequestBody dto: DeviceCreateDTO
    ): ResponseEntity<Void> {
        service.create(dto)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "更新Device", description = "更新Device信息")
    @PutMapping(value = ["/"])
    fun update(
        @Parameter(description = "Device信息", required = true)
        @Valid @RequestBody dto: DeviceUpdateDTO
    ): ResponseEntity<Void> {
        service.update(dto)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "分页查询Device", description = "分页查询Device列表")
    @GetMapping(value = ["/page"])
    fun findPage(
        @Parameter(description = "页码", required = false) @RequestParam(defaultValue = "1") currentPage: Int,
        @Parameter(description = "每页数量", required = false) @RequestParam(defaultValue = "10") pageSize: Int,
        specification: DeviceSpecification
    ): ResponseEntity<Page<DeviceListDTO>> {
        val pageRequest = PageRequest.of(currentPage, pageSize).oneIndex()
        val pageResult = service.findPage(pageRequest, specification)
        return ResponseEntity.ok(pageResult)
    }
}
