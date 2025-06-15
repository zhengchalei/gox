package com.zhengchalei.gox.modules.iot.device.controller

import com.zhengchalei.gox.R
import com.zhengchalei.gox.framework.config.oneIndex
import com.zhengchalei.gox.modules.iot.device.entity.dto.*
import com.zhengchalei.gox.modules.iot.device.service.MqttAclService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * MQTT访问控制Controller
 *
 * @author zhengchalei
 */
@Tag(name = "MQTT访问控制管理", description = "MQTT访问控制相关操作")
@Validated
@RestController
@RequestMapping("/api/iot/mqtt-acl")
class MqttAclController(private val service: MqttAclService) {

    @Operation(summary = "根据ID查询MQTT访问控制", description = "通过MQTT访问控制ID获取MQTT访问控制详细信息")
    @GetMapping(value = ["/{id}"])
    fun findById(
        @Parameter(description = "MqttAclID", required = true)
        @PathVariable id: Long
    ): R<MqttAclDetailDTO> {
        val result = service.findById(id)
        return R.success(result)
    }

    @Operation(summary = "删除MQTT访问控制", description = "根据MQTT访问控制ID删除MQTT访问控制")
    @DeleteMapping(value = ["/{id}"])
    fun deleteById(
        @Parameter(description = "MqttAclID", required = true)
        @PathVariable id: Long
    ): R<Void> {
        service.deleteById(id)
        return R.success()
    }

    @Operation(summary = "创建MQTT访问控制", description = "创建新MQTT访问控制")
    @PostMapping(value = [""])
    fun create(
        @Parameter(description = "MqttAcl信息", required = true)
        @Valid @RequestBody dto: MqttAclCreateDTO
    ): R<Void> {
        service.create(dto)
        return R.success()
    }

    @Operation(summary = "更新MQTT访问控制", description = "更新MQTT访问控制信息")
    @PutMapping(value = [""])
    fun update(
        @Parameter(description = "MqttAcl信息", required = true)
        @Valid @RequestBody dto: MqttAclUpdateDTO
    ): R<Void> {
        service.update(dto)
        return R.success()
    }

    @Operation(summary = "分页查询MQTT访问控制", description = "分页查询MQTT访问控制列表")
    @GetMapping(value = ["/page"])
    fun findPage(
        @Parameter(description = "页码", required = false) @RequestParam(defaultValue = "1") currentPage: Int,
        @Parameter(description = "每页数量", required = false) @RequestParam(defaultValue = "10") pageSize: Int,
        specification: MqttAclSpecification
    ): R<Page<MqttAclListDTO>> {
        val pageRequest = PageRequest.of(currentPage, pageSize).oneIndex()
        val pageResult = service.findPage(pageRequest, specification)
        return R.success(pageResult)
    }
}
