package com.zhengchalei.gox.modules.iot.device.controller

import com.zhengchalei.gox.R
import com.zhengchalei.gox.framework.config.oneIndex
import com.zhengchalei.gox.modules.iot.device.entity.dto.*
import com.zhengchalei.gox.modules.iot.device.service.DeviceService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * 设备Controller
 *
 * @author zhengchalei
 */
@Tag(name = "设备管理", description = "设备相关操作")
@Validated
@RestController
@RequestMapping("/api/iot/device")
class DeviceController(private val service: DeviceService) {

    @Operation(summary = "根据ID查询设备", description = "通过设备ID获取设备详细信息")
    @GetMapping(value = ["/{id}"])
    fun findById(
        @Parameter(description = "DeviceID", required = true)
        @PathVariable id: Long
    ): R<DeviceDetailDTO> {
        val result = service.findById(id)
        return R.success(result)
    }

    @Operation(summary = "删除设备", description = "根据设备ID删除设备")
    @DeleteMapping(value = ["/{id}"])
    fun deleteById(
        @Parameter(description = "DeviceID", required = true)
        @PathVariable id: Long
    ): R<Void> {
        service.deleteById(id)
        return R.success()
    }

    @Operation(summary = "创建设备", description = "创建新设备")
    @PostMapping(value = [""])
    fun create(
        @Parameter(description = "Device信息", required = true)
        @Valid @RequestBody dto: DeviceCreateDTO
    ): R<Void> {
        service.create(dto)
        return R.success()
    }

    @Operation(summary = "更新设备", description = "更新设备信息")
    @PutMapping(value = [""])
    fun update(
        @Parameter(description = "Device信息", required = true)
        @Valid @RequestBody dto: DeviceUpdateDTO
    ): R<Void> {
        service.update(dto)
        return R.success()
    }

    @Operation(summary = "分页查询设备", description = "分页查询设备列表")
    @GetMapping(value = ["/page"])
    fun findPage(
        @Parameter(description = "页码", required = false) @RequestParam(defaultValue = "1") currentPage: Int,
        @Parameter(description = "每页数量", required = false) @RequestParam(defaultValue = "10") pageSize: Int,
        specification: DeviceSpecification
    ): R<Page<DeviceListDTO>> {
        val pageRequest = PageRequest.of(currentPage, pageSize).oneIndex()
        val result = service.findPage(pageRequest, specification)
        return R.success(result)
    }
}
