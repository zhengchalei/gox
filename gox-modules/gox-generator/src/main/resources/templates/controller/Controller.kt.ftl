package ${packageName}.controller

import com.zhengchalei.gox.R
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

/**
 * ${entityComment}Controller
 *
 * @author zhengchalei
 */
@Tag(name = "${entityComment}管理", description = "${entityComment}相关操作")
@Validated
@RestController
@RequestMapping("/api/${moduleName}/${entityName?uncap_first?replace('([a-z])([A-Z])', '$1-$2', 'r')?lower_case}")
class ${entityName}Controller(private val service: ${entityName}Service) {

    @Operation(summary = "根据ID查询${entityComment}", description = "通过${entityComment}ID获取${entityComment}详细信息")
    @GetMapping(value = ["/{id}"])
    fun findById(
        @Parameter(description = "${entityName}ID", required = true)
        @PathVariable id: Long
    ): R<${entityName}DetailDTO> {
        val result = service.findById(id)
        return R.success(result)
    }

    @Operation(summary = "删除${entityComment}", description = "根据${entityComment}ID删除${entityComment}")
    @DeleteMapping(value = ["/{id}"])
    fun deleteById(
        @Parameter(description = "${entityName}ID", required = true)
        @PathVariable id: Long
    ): R<Void> {
        service.deleteById(id)
        return R.success()
    }

    @Operation(summary = "创建${entityComment}", description = "创建新${entityComment}")
    @PostMapping(value = [""])
    fun create(
        @Parameter(description = "${entityName}信息", required = true)
        @Valid @RequestBody dto: ${entityName}CreateDTO
    ): R<Void> {
        service.create(dto)
        return R.success()
    }

    @Operation(summary = "更新${entityComment}", description = "更新${entityComment}信息")
    @PutMapping(value = [""])
    fun update(
        @Parameter(description = "${entityName}信息", required = true)
        @Valid @RequestBody dto: ${entityName}UpdateDTO
    ): R<Void> {
        service.update(dto)
        return R.success()
    }

    @Operation(summary = "分页查询${entityComment}", description = "分页查询${entityComment}列表")
    @GetMapping(value = ["/page"])
    fun findPage(
        @Parameter(description = "页码", required = false) @RequestParam(defaultValue = "1") currentPage: Int,
        @Parameter(description = "每页数量", required = false) @RequestParam(defaultValue = "10") pageSize: Int,
        specification: ${entityName}Specification
    ): R<Page<${entityName}ListDTO>> {
        val pageRequest = PageRequest.of(currentPage, pageSize).oneIndex()
        val result = service.findPage(pageRequest, specification)
        return R.success(result)
    }
}
