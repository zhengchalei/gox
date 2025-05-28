package ${packageName}.controller

import com.zhengchalei.gox.framework.config.oneIndex
import ${packageName}.entity.dto.*
import ${packageName}.service.${entityName}Service
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Tag(name = "${entityName}管理", description = "${entityName}相关操作")
@Validated
@RestController
@RequestMapping("/api/v1/sys/${entityName?lower_case}")
class ${entityName}Controller(private val service: ${entityName}Service) {

    @Operation(summary = "根据ID查询${entityName}", description = "通过${entityName}ID获取${entityName}详细信息")
    @GetMapping(value = ["/{id}"])
    fun findById(
        @Parameter(description = "${entityName}ID", required = true)
        @PathVariable id: Long
    ): ResponseEntity<${entityName}DetailDTO> {
        val result = service.findById(id)
        return ResponseEntity.ok(result)
    }

    @Operation(summary = "删除${entityName}", description = "根据${entityName}ID删除${entityName}")
    @DeleteMapping(value = ["/{id}"])
    fun deleteById(
        @Parameter(description = "${entityName}ID", required = true)
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        service.deleteById(id)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "创建${entityName}", description = "创建新${entityName}")
    @PostMapping(value = ["/"])
    fun create(
        @Parameter(description = "${entityName}信息", required = true)
        @Valid @RequestBody dto: ${entityName}CreateDTO
    ): ResponseEntity<Void> {
        service.create(dto)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "更新${entityName}", description = "更新${entityName}信息")
    @PutMapping(value = ["/"])
    fun update(
        @Parameter(description = "${entityName}信息", required = true)
        @Valid @RequestBody dto: ${entityName}UpdateDTO
    ): ResponseEntity<Void> {
        service.update(dto)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "分页查询${entityName}", description = "分页查询${entityName}列表")
    @GetMapping(value = ["/page"])
    fun findPage(
        @Parameter(description = "页码", required = false) @RequestParam(defaultValue = "1") currentPage: Int,
        @Parameter(description = "每页数量", required = false) @RequestParam(defaultValue = "10") pageSize: Int,
        specification: ${entityName}Specification
    ): ResponseEntity<Page<${entityName}ListDTO>> {
        val pageRequest = PageRequest.of(currentPage, pageSize).oneIndex()
        val pageResult = service.findPage(pageRequest, specification)
        return ResponseEntity.ok(pageResult)
    }
}
