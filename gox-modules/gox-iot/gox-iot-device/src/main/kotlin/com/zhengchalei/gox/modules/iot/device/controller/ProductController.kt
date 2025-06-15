package com.zhengchalei.gox.modules.iot.device.controller

import com.zhengchalei.gox.framework.config.oneIndex
import com.zhengchalei.gox.modules.iot.device.entity.dto.*
import com.zhengchalei.gox.modules.iot.device.service.ProductService
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
 * 产品Controller
 *
 * @author zhengchalei
 */
@Tag(name = "产品管理", description = "产品相关操作")
@Validated
@RestController
@RequestMapping("/api/iot/product")
class ProductController(private val service: ProductService) {

    @Operation(summary = "根据ID查询产品", description = "通过产品ID获取产品详细信息")
    @GetMapping(value = ["/{id}"])
    fun findById(
        @Parameter(description = "ProductID", required = true)
        @PathVariable id: Long
    ): ResponseEntity<ProductDetailDTO> {
        val result = service.findById(id)
        return ResponseEntity.ok(result)
    }

    @Operation(summary = "删除产品", description = "根据产品ID删除产品")
    @DeleteMapping(value = ["/{id}"])
    fun deleteById(
        @Parameter(description = "ProductID", required = true)
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        service.deleteById(id)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "创建产品", description = "创建新产品")
    @PostMapping(value = [""])
    fun create(
        @Parameter(description = "Product信息", required = true)
        @Valid @RequestBody dto: ProductCreateDTO
    ): ResponseEntity<Void> {
        service.create(dto)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "更新产品", description = "更新产品信息")
    @PutMapping(value = [""])
    fun update(
        @Parameter(description = "Product信息", required = true)
        @Valid @RequestBody dto: ProductUpdateDTO
    ): ResponseEntity<Void> {
        service.update(dto)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "分页查询产品", description = "分页查询产品列表")
    @GetMapping(value = ["/page"])
    fun findPage(
        @Parameter(description = "页码", required = false) @RequestParam(defaultValue = "1") currentPage: Int,
        @Parameter(description = "每页数量", required = false) @RequestParam(defaultValue = "10") pageSize: Int,
        specification: ProductSpecification
    ): ResponseEntity<Page<ProductListDTO>> {
        val pageRequest = PageRequest.of(currentPage, pageSize).oneIndex()
        val pageResult = service.findPage(pageRequest, specification)
        return ResponseEntity.ok(pageResult)
    }
}
